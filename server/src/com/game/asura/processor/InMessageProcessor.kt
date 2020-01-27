package com.game.asura.processor

import com.game.asura.*
import com.game.asura.account.CachedAccount
import com.game.asura.account.PlayerAccount
import com.game.asura.card.CardEffect
import com.game.asura.card.CardType
import com.game.asura.card.Minion
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
                    println("${Thread.currentThread().name} Match found for player:${player.playerName}")
                    account.setMatch(match)
                    player.initializeDeck()
                    val opponentName = match.getOpponentName(accountName)
                    val matchInfo = MatchStartOut(account.getChannelWriter(), accountName, opponentName)
                    messageQueue.addMessage(matchInfo)

                    //send start turn to a player
                    val startTurn = StartTurnOut(account.getChannelWriter())
                    messageQueue.addMessage(startTurn)
                    //schedule end turn X seconds from now or until we receive such info player client.
                    val endTurn = EndTurnIn(account.getAccountKey(), matchTurn = match.getMatchTurn())
                    messageQueue.addMessage(endTurn, SECOND_PER_TURN * ONE_SECOND_MILLIS)
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

            is CardPlayedIn -> {

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
                if (cardInHand.getCardType() == CardType.TARGET_SPELL && message.cardTarget == null) {
                    val cardName = cardInfoStore.getCardInfo(message.cardPrimaryId)?.name
                    println("Error, card:$cardName from player:$accountName was played no target.")
                    return
                }
                //validation is finish, can now play card so send back message to all player
                val cardPlayed = CardPlayedOut(account.getChannelWriter(), accountName, cardInHand, message.cardTarget, message.boardPosition)
                messageQueue.addMessage(cardPlayed)

                player.playCard(cardInHand, message.boardPosition)
                if (cardInHand.getCardType() == CardType.MONSTER) {
                    if (player.currentPhase == Phase.MAIN) {
                        player.currentPhase = Phase.ATTACK
                    }
                    val phaseChange = PhaseChangeOut(account.getChannelWriter(), Phase.ATTACK)
                    messageQueue.addMessage(phaseChange)
                }
                //check for double to merge
                if (cardInHand is ServerMinionCard && cardInHand.canEvolve()) {
                    val dupeList = player.boardManager.findDuplicate(cardInHand.getPrimaryId())
                    if (dupeList.size >= MERGE_IF_DUPLICATE_REACH) {
                        val evInfo = cardInfoStore.getCardInfo(cardInHand.evolveId!!) ?: return
                        evInfo.health ?: return
                        evInfo.maxHealth ?: return
                        val evolved = ServerMinionCard(primaryId = evInfo.id, cardCost = evInfo.cost, cardType = evInfo.cardType,
                                attack = evInfo.attack ?: 0, health = evInfo.health, maxHealth = evInfo.maxHealth,
                                evolveId = evInfo.evolveId)
                        match.addCardToCache(evolved)
                        player.boardManager.mergeCard(dupeList, evolved, INVALID_MINION_CARD)
                        //evolved monster should take position of the 1st minion of that type that was on board
                        val evolvePos = dupeList.stream().filter { c -> c.dupeCard.getSecondayId() != cardInHand.getSecondayId() }.findFirst().get().boardIdx
                        val minionEvolved = MonsterEvolveOut(channelWriter = account.getChannelWriter(),
                                card = evolved, boardPosition = evolvePos, firstMonsterId = dupeList[0].dupeCard.getSecondayId(),
                                secondMonsterId = dupeList[1].dupeCard.getSecondayId())
                        messageQueue.addMessage(minionEvolved)
                    }
                }

                val changedFields: MutableList<ChangedField> = ArrayList()
                if (cardInHand.getCost() > 0) {
                    println("cardCost:${cardInHand.getCost()},currentMana:${player.currentMana}")
                    val playerInfoOut = PlayerInfoOut(channelWriter = account.getChannelWriter(), accoutName = accountName, currentMana = player.currentMana, maxMana = player.maxMana, playerHealth = player.playerLifePoint)
                    messageQueue.addMessage(playerInfoOut)
                }


                val effects = cardInHand.getEffect()
                //only handle target spell effect for now
                if (message.cardTarget == null) {
                    return
                }
                val target = match.getCard(message.cardTarget) ?: return
                if (target !is Minion) {
                    return
                }
                for (effect in effects) {
                    if (effect == CardEffect.DEAL_DMG) {
                        target.takeDamage(3)
                        val healthField = ChangedField(MessageField.CARD_HEALTH, target.getHealth())
                        changedFields.add(healthField)
                        val playerInfoOut = CardInfoOut(channelWriter = account.getChannelWriter(), accountName = accountName, card = target, changedFields = changedFields)
                        messageQueue.addMessage(playerInfoOut)
                        continue
                    }
                }
                //check target died and process it
                if (!target.isAlive()) {
                    match.removeCardFromCache(target)
                    println("Board size before remove:${player.boardManager.cardsOnBoard()}")
                    player.boardManager.removeCard(target)
                    println("Board size after remove:${player.boardManager.cardsOnBoard()}")
                    //send monster destroy message?
                }
            }
            is EndTurnIn -> {
                if (message.matchTurn != null) {
                    //server is end the turn due to time being over limit
                    val account = accountCache.getAccount(message.accountKey)
                    val accountName = account?.getAccountName()
                    if (accountName == null) {
                        println("Unable to find accountName in cache with key:${message.accountKey}.")
                        return
                    }
                    //look if player already had ended turn
                    val match = matchFinder.findMatch(account.getCurrentMatchId()) ?: return
                    if (message.matchTurn < match.getMatchTurn()) {
                        //player has already send end turn before timer up, disregard this internal end turn msg.
                        println("Disregarding end turn timeout scheduled since player sent end turn already for match:${account.getCurrentMatchId()} turn:${message.matchTurn}")
                        return
                    }
                    //force time out as we did not receive an end turn message in time
                    match.increaseMatchTurn()


                    val endTurnOut = EndTurnOut(account.getChannelWriter())
                    messageQueue.addMessage(endTurnOut)
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

                val bResult = match.processAttack(accountName) ?: return
                if (bResult.defenderWasDamaged()) {
                    //temp disable as we don't have 2nd player connected in dev
                    // val defenderAccount = accountCache.getAccount(bResult.defender.accountKey) ?: return
                    val dPlayer = bResult.defender
                    /*val playerInfoOut = PlayerInfoOut(defenderAccount.getChannelWriter(), dPlayer.playerName,
                            dPlayer.currentMana, dPlayer.maxMana, dPlayer.playerLifePoint)
                    messageQueue.addMessage(playerInfoOut)*/

                    val playerInfoOut = PlayerInfoOut(account.getChannelWriter(), dPlayer.playerName,
                            dPlayer.currentMana, dPlayer.maxMana, dPlayer.playerLifePoint)
                    messageQueue.addMessage(playerInfoOut)

                    match.setPlayerNextPhase(accountName, Phase.POST_ATTACK)
                    val phaseOut = PhaseChangeOut(account.getChannelWriter(), Phase.POST_ATTACK)
                    messageQueue.addMessage(phaseOut)
                }
                /*val changedFieldsAttacker: MutableList<ChangedField> = ArrayList()
                val changedFieldsDefender: MutableList<ChangedField> = ArrayList()
                val healthFieldA = ChangedField(MessageField.CARD_HEALTH, attacker.getHealth())
                val healthFieldD = ChangedField(MessageField.CARD_HEALTH, defender.getHealth())
                changedFieldsAttacker.add(healthFieldA)
                changedFieldsDefender.add(healthFieldD)
                var cardInfoOut = CardInfoOut(channelWriter = account.getChannelWriter(), accountName = accountName,
                        card = attacker, changedFields = changedFieldsAttacker)
                messageQueue.addMessage(cardInfoOut)
                cardInfoOut = CardInfoOut(channelWriter = account.getChannelWriter(), accountName = accountName,
                        card = defender, changedFields = changedFieldsDefender)
                messageQueue.addMessage(cardInfoOut)*/
            }
            else -> {
                println("Error unable to processIn message:$message")
            }
        }
    }
}