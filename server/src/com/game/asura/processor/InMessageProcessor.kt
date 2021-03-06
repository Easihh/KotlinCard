package com.game.asura.processor

import com.game.asura.*
import com.game.asura.account.CachedAccount
import com.game.asura.account.PlayerAccount
import com.game.asura.messagein.*
import com.game.asura.messageout.*
import com.game.asura.messaging.MessageField
import com.game.asura.parsing.DecodedMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class InMessageProcessor(private val messageQueue: InsertableQueue,
                         private val accountCache: CachedAccount,
                         private val matchFinder: MatchFinder,
                         private val cardInfoStore: CardInfoStore,
                         private val processorContext: CoroutineContext) {

    fun onMessage(message: DecodedMessage) {
        when (message) {
            is LoginRequestIn -> {
                //login validation here

                //login accept
                val account = message.playerAccount as PlayerAccount
                accountCache.addActiveAccount(account.getAccountKey(), account)
                println("Added accountKey:${account.getAccountKey()} with name:${account.getAccountName()} to activePlayer list.")
                val reply = LoginRequestReplyOut(account.getChannelWriter(), LoginStatus.CONNECTED)
                messageQueue.addMessage(reply)
            }
            is GameRequestIn -> {
                val account = accountCache.getAccount(message.accountKey)
                val accountName = account?.getAccountName()
                if (accountName == null) {
                    println("Unable to find accountName in cache with key:${message.accountKey}")
                    return
                }
                val player = ServerPlayer(accountName, message.accountKey, cardInfoStore)
                GlobalScope.launch(processorContext) {
                    val match = matchFinder.addPlayer(player)
                    println("Match found for player:${player.playerName}")
                    account.setMatch(match)
                    account.getPlayer()
                    player.initializeDeck()
                    val opponentName = match.getOpponentName(accountName)
                    val matchInfo = MatchStartOut(account.getChannelWriter(), accountName, opponentName)
                    messageQueue.addMessage(matchInfo)

                    //send start turn to a player
                    if (match.getCurrentPlayerTurn().playerName == player.playerName) {
                        val startTurn = StartTurnOut(account.getChannelWriter(), player.currentPhase)
                        messageQueue.addMessage(startTurn)
                        //schedule end turn X seconds from now or until we receive such info player client.
                        val endTurn = EndTurnIn(account.getAccountKey(), matchTurn = match.getMatchTurn())
                        messageQueue.addMessage(endTurn, SECOND_PER_TURN * ONE_SECOND_MILLIS)
                    }

                    //send initial draws
                    for (x in 0..3) {
                        val cardDrawn = player.draw()
                        cardDrawn?.let {
                            val cardDrawnOut = CardDrawnOut(account.getChannelWriter(), cardDrawn, player.cardRemaining())
                            match.addCardToCache(cardDrawn)
                            messageQueue.addMessage(cardDrawnOut)
                        }
                    }
                }
            }

            is SpellCardPlayedIn -> {
                val account = accountCache.getAccount(message.accountKey)
                val accountName = account?.getAccountName()
                if (accountName == null) {
                    println("Unable to find accountName in cache with key:${message.accountKey}.")
                    return
                }
                val match = matchFinder.findMatch(account.getCurrentMatchId())
                if (match == null) {
                    println("Unable to find match with id:${account.getCurrentMatchId()}.")
                    return
                }
                val cardBeingPlayed = match.getCard(message.cardSecondaryId) ?: return
                if (cardBeingPlayed !is ServerSpellCard) {
                    println("Error, card $cardBeingPlayed is not a Spell.")
                    return
                }
                if (match.getCurrentPlayerTurn().playerName != accountName) {
                    println("Player $accountName is trying to play card $cardBeingPlayed but it is not his turn yet.Disregarding request.")
                    return
                }
                val player = match.getCurrentPlayerTurn()

                player.playCard(cardBeingPlayed)
                println("Player ${player.playerName} is playing card $cardBeingPlayed.")

                val cardPlayed = SpellCardPlayedOut(account.getChannelWriter(), accountName, cardBeingPlayed, null)
                messageQueue.addMessage(cardPlayed)

                //for now hard core draw 2 card as spell
                for (x in 0..2) {
                    val cardDrawn = player.draw()
                    cardDrawn?.let {
                        val cardDrawnOut = CardDrawnOut(account.getChannelWriter(), cardDrawn, player.cardRemaining())
                        match.addCardToCache(cardDrawn)
                        messageQueue.addMessage(cardDrawnOut)
                    }
                }
            }

            is MonsterCardPlayedIn -> {
                val account = accountCache.getAccount(message.accountKey)
                val accountName = account?.getAccountName()
                if (accountName == null) {
                    println("Unable to find accountName in cache with key:${message.accountKey}.")
                    return
                }
                val match = matchFinder.findMatch(account.getCurrentMatchId())
                if (match == null) {
                    println("Unable to find match with id:${account.getCurrentMatchId()}.")
                    return
                }
                if (match.getCurrentPlayerTurn().playerName != accountName) {
                    println("Player $accountName is trying to play a card but it is not his turn yet.Disregarding request.")
                    return
                }
                val opponentName = match.getOpponentName(accountName)
                val opponentPlayer = match.getPlayer(opponentName) ?: return
                val opponentAccount = accountCache.getAccount(opponentPlayer.accountKey) ?: return

                val player = match.getPlayer(accountName)
                if (player == null) {
                    println("Unable to find player in match:${match.matchId} with key:$accountName.")
                    return
                }
                val cardInHand = player.handManager.getCardFromHand(message.cardSecondaryId)
                if (cardInHand == null) {
                    println("Unable to find card with secondaryId:${message.cardSecondaryId} in player:$accountName hand.")
                    return
                }
                if (cardInHand !is ServerMinionCard) {
                    println("Error, card $cardInHand is not a Monster.")
                    return
                }
                //validation is finish, can now play card so send back message to both player
                val cardPlayed = MonsterCardPlayedOut(account.getChannelWriter(), accountName, cardInHand, message.boardPosition)
                messageQueue.addMessage(cardPlayed)

                val cardPlayedOpp = MonsterCardPlayedOut(opponentAccount.getChannelWriter(), accountName, cardInHand, message.boardPosition)
                messageQueue.addMessage(cardPlayedOpp)


                player.playMinionCard(cardInHand, message.boardPosition)
                if (player.currentPhase == Phase.MAIN && !cardInHand.isSummonSick()) {
                    player.currentPhase = Phase.ATTACK
                    val phaseChange = PhaseChangeOut(account.getChannelWriter(), Phase.ATTACK)
                    messageQueue.addMessage(phaseChange)
                }
                //check for double to merge
                if (cardInHand.canEvolve()) {
                    val dupeList = player.boardManager.findDuplicate(cardInHand.getPrimaryId())
                    if (dupeList.size >= MERGE_IF_DUPLICATE_REACH) {
                        val evInfo = cardInfoStore.getCardInfo(cardInHand.evolveId!!) ?: return
                        evInfo.health ?: return
                        evInfo.maxHealth ?: return
                        val evolved = ServerMinionCard(primaryId = evInfo.id, cardCost = evInfo.cost, cardType = evInfo.cardType,
                                attack = evInfo.attack ?: 0, health = evInfo.health, maxHealth = evInfo.maxHealth,
                                evolveId = evInfo.evolveId, owner = player.playerName)
                        match.addCardToCache(evolved)
                        player.boardManager.mergeCard(dupeList, evolved, cardInHand.getSecondayId())
                        //evolved monster should take position of the 1st minion of that type that was on board
                        val evolvePos = dupeList.stream().filter { c -> c.dupeCard.getSecondayId() != cardInHand.getSecondayId() }.findFirst().get().boardIdx
                        val minionEvolved = MonsterEvolveOut(channelWriter = account.getChannelWriter(),
                                card = evolved, boardPosition = evolvePos, firstMonsterId = dupeList[0].dupeCard.getSecondayId(),
                                secondMonsterId = dupeList[1].dupeCard.getSecondayId(), accountName = accountName)
                        messageQueue.addMessage(minionEvolved)
                        val minionEvolvedOpp = MonsterEvolveOut(channelWriter = opponentAccount.getChannelWriter(),
                                card = evolved, boardPosition = evolvePos, firstMonsterId = dupeList[0].dupeCard.getSecondayId(),
                                secondMonsterId = dupeList[1].dupeCard.getSecondayId(), accountName = accountName)
                        messageQueue.addMessage(minionEvolvedOpp)
                    }
                }

                if (cardInHand.getCost() > 0) {
                    println("cardCost:${cardInHand.getCost()},currentMana:${player.currentMana}")
                    val playerInfoOut = PlayerInfoOut(channelWriter = account.getChannelWriter(), accoutName = accountName, currentMana = player.currentMana, maxMana = player.maxMana, playerHealth = player.playerLifePoint)
                    messageQueue.addMessage(playerInfoOut)
                }
            }
            is EndTurnIn -> {
                val account = accountCache.getAccount(message.accountKey) ?: return
                val match = matchFinder.findMatch(account.getCurrentMatchId()) ?: return
                if (message.matchTurn != null) {
                    //server is ending the turn due to time being over limit

                    //look if player already had ended turn
                    if (message.matchTurn < match.getMatchTurn()) {
                        //player has already send end turn before timer up, disregard this internal end turn msg.
                        println("Disregarding end turn timeout scheduled since player sent end turn already for match:${account.getCurrentMatchId()} turn:${message.matchTurn}")
                        return
                    }
                    //force time out as we did not receive an end turn message in time
                    val currentPlayer = match.getCurrentPlayerTurn()
                    val currentPlayerAccount = accountCache.getAccount(currentPlayer.accountKey) ?: return
                    match.endTurn()
                    //next player
                    val nextPlayer = match.getCurrentPlayerTurn()
                    val nextPlayerAccount = accountCache.getAccount(nextPlayer.accountKey) ?: return
                    val endTurnOut = EndTurnOut(currentPlayerAccount.getChannelWriter())
                    messageQueue.addMessage(endTurnOut)
                    val turnStart = StartTurnOut(nextPlayerAccount.getChannelWriter(), nextPlayer.currentPhase)
                    messageQueue.addMessage(turnStart)

                    val updates = nextPlayerAccount.getPlayer()?.boardManager?.updateSummonIllness() ?: return

                    if (updates.isNotEmpty()) {
                        val changedField: List<ChangedField> = listOf(ChangedField(MessageField.SUMMON_ILLNESS, 'F'))
                        for (minion in updates) {
                            val cardInfo = CardInfoOut(nextPlayerAccount.getChannelWriter(), nextPlayerAccount.getAccountName(), minion, changedField)
                            messageQueue.addMessage(cardInfo)
                        }
                    }
                    return
                }
                //player sent end of turn

                val currentPlayer = match.getCurrentPlayerTurn()
                val currentPlayerAccount = accountCache.getAccount(currentPlayer.accountKey) ?: return
                match.endTurn()
                val endTurnOut = EndTurnOut(currentPlayerAccount.getChannelWriter())
                messageQueue.addMessage(endTurnOut)

                //next player
                val nextPlayer = match.getCurrentPlayerTurn()
                val nextPlayerAccount = accountCache.getAccount(nextPlayer.accountKey) ?: return

                val turnStart = StartTurnOut(nextPlayerAccount.getChannelWriter(), nextPlayer.currentPhase)
                messageQueue.addMessage(turnStart)
                val updates = nextPlayerAccount.getPlayer()?.boardManager?.updateSummonIllness() ?: return

                if (updates.isNotEmpty()) {
                    val changedField: List<ChangedField> = listOf(ChangedField(MessageField.SUMMON_ILLNESS, 'F'))
                    for (minion in updates) {
                        val cardInfo = CardInfoOut(nextPlayerAccount.getChannelWriter(), nextPlayerAccount.getAccountName(), minion, changedField)
                        messageQueue.addMessage(cardInfo)
                    }
                }
            }

            is AttackIn -> {
                val account = accountCache.getAccount(message.accountKey)
                val accountName = account?.getAccountName()
                if (accountName == null) {
                    println("Unable to find accountName in cache with key:${message.accountKey}.")
                    return
                }

                val match = matchFinder.findMatch(account.getCurrentMatchId()) ?: return
                val enemyName = match.getOpponentName(account.getAccountName())
                val enemyPlayer = match.getPlayer(enemyName) ?: return
                val enemyAccount = accountCache.getAccount(enemyPlayer.accountKey) ?: return
                val bResult = match.processAttack(accountName) ?: return
                //send monster info resulting from battle ie health update
                for (monster in bResult.participant) {
                    val changedFields: MutableList<ChangedField> = ArrayList()
                    changedFields.add(ChangedField(MessageField.CARD_HEALTH, monster.getHealth()))
                    val monsterUpdate = CardInfoOut(account.getChannelWriter(), accountName, monster, changedFields)
                    messageQueue.addMessage(monsterUpdate)
                    val monsterOppUpdate = CardInfoOut(enemyAccount.getChannelWriter(), enemyName, monster, changedFields)
                    messageQueue.addMessage(monsterOppUpdate)
                    if (!monster.isAlive()) {
                        val monsterDeath = MonsterDeathOut(account.getChannelWriter(), monster)
                        val monsterDeathOpp = MonsterDeathOut(enemyAccount.getChannelWriter(), monster)
                        messageQueue.addMessage(monsterDeath)
                        messageQueue.addMessage(monsterDeathOpp)
                    }
                }
                if (bResult.defenderWasDamaged()) {
                    val dPlayer = bResult.defender
                    //inform both player of healthPoint change of defender

                    val playerInfoOut = PlayerInfoOut(account.getChannelWriter(), dPlayer.playerName,
                            dPlayer.currentMana, dPlayer.maxMana, dPlayer.playerLifePoint)
                    messageQueue.addMessage(playerInfoOut)

                    val enemyInfoOut = PlayerInfoOut(enemyAccount.getChannelWriter(), dPlayer.playerName,
                            dPlayer.currentMana, dPlayer.maxMana, dPlayer.playerLifePoint)
                    messageQueue.addMessage(enemyInfoOut)
                }
                match.setPlayerNextPhase(accountName, Phase.POST_ATTACK)
                val phaseOut = PhaseChangeOut(account.getChannelWriter(), Phase.POST_ATTACK)
                messageQueue.addMessage(phaseOut)
            }
            else -> {
                println("Error unable to processIn message:$message")
            }
        }
    }
}