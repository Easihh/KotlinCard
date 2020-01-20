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

class InMessageProcessor(private val messageQueue: InsertableQueue,
                         private val accountCache: CachedAccount,
                         private val matchFinder: MatchFinder,
                         private val cardInfoStore: CardInfoStore) {

    fun onMessage(message: DecodedMessage) {
        when (message) {
            is LoginRequestIn -> {
                //login validation here

                //login accept
                val account = message.getPlayerAccount() as PlayerAccount
                accountCache.addActiveAccount(account.getAccountKey(), account)
                println("Added accountKey:${account.getAccountKey()} with name:${account.getAccountName()} to activePlayer list.")
            }
            is GameRequestIn -> {
                val account = accountCache.getAccount(message.accountKey)
                val accountName = account?.getAccountName()
                if (accountName == null) {
                    println("Unable to find accountName in cache with key:${message.accountKey}")
                    return
                }
                val player = ServerPlayer(accountName, message.accountKey, 5, cardInfoStore = cardInfoStore)
                val enemyPlayer = ServerPlayer("Enemy", "enemyAccountKey", 5, cardInfoStore = cardInfoStore)
                player.initializeDeck()
                enemyPlayer.initializeDeck()
                val match = Match()
                account.setMatch(match)
                match.addPlayer(accountName, player)
                match.addPlayer("Enemy", enemyPlayer)
                matchFinder.addMatch(match)
                //1st player
                val matchInfo = MatchStartOut(account.getChannelWriter(), accountName, "Enemy", player.heroPlayer, enemyPlayer.heroPlayer)
                //2nd player
                //val matchInfo2 = MatchStartOut(account.getChannelWriter(), "Enemy", accountName, enemyPlayer.heroPlayer, player.heroPlayer)
                messageQueue.addMessage(matchInfo)
                //messageQueue.addMessage(matchInfo2)
                //send start turn to a player
                val startTurn = StartTurnOut(account.getChannelWriter())
                messageQueue.addMessage(startTurn)
                //schedule end turn X seconds from now or until we receive such info player client.
                val endTurn = EndTurnIn(account.getAccountKey(), matchTurn = match.getMatchTurn())
                messageQueue.addMessage(endTurn, SECOND_PER_TURN * ONE_SECOND_MILLIS)
                //send initial draws
                for (x in 0..3) {
                    val cardDrawn = player.draw() ?: return
                    val cardDrawnOut = CardDrawnOut(account.getChannelWriter(), cardDrawn, player.cardRemaining())
                    match.addCardToCache(cardDrawn)
                    messageQueue.addMessage(cardDrawnOut)

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

                val changedFields: MutableList<ChangedField> = ArrayList()
                if (cardInHand.getCost() > 0) {
                    println("cardCost:${cardInHand.getCost()},currentMana:${player.heroPlayer.getCurrentMana()}")
                    val playerInfoOut = PlayerInfoOut(channelWriter = account.getChannelWriter(), accoutName = accountName, currentMana = player.heroPlayer.getCurrentMana(), maxMana = player.heroPlayer.getMaxMana())
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

            is MonsterAttackIn -> {
                val account = accountCache.getAccount(message.accountKey)
                val accountName = account?.getAccountName()
                if (accountName == null) {
                    println("Unable to find accountName in cache with key:${message.accountKey}.")
                    return
                }
                val match = matchFinder.findMatch(account.getCurrentMatchId()) ?: return
                val attacker = match.getCard(message.secondaryId) ?: return
                val defender = match.getCard(message.target) ?: return
                if (attacker !is Minion || defender !is Minion) {
                    return
                }
                match.monsterAttack(attacker, defender)
                val changedFieldsAttacker: MutableList<ChangedField> = ArrayList()
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
                messageQueue.addMessage(cardInfoOut)

                if (attacker.getCardType() == CardType.HERO && !attacker.isAlive()) {

                    // attacker hero died, process end match
                    val matchEnd = MatchEndOut(account.getChannelWriter(), accountName = accountName,
                            matchResult = MatchResult.LOSS)
                    messageQueue.addMessage(matchEnd)
                    //disable for now as we are testing with a single connected player in a match

                    /*
                    val defenderPlayer = match.getCardOwner(defender.getSecondayId()) ?: return
                    val defenderAccount = accountCache.getAccount(defenderPlayer.accountKey) ?: return
                    matchEnd = MatchEndOut(defenderAccount.getChannelWriter(), accountName = accountName,
                            matchResult = MatchResult.WIN)*/
                    //messageQueue.addMessage(matchEnd)
                    matchFinder.removeMatch(match)
                }
                if (defender.getCardType() == CardType.HERO && !defender.isAlive()) {
                    println("Defending hero is dead.Ending match:${match.matchId}")
                    // defender hero died, process end match

                    //disable for now as we are testing with a single connected player in a match
                    /*val defenderPlayer = match.getCardOwner(defender.getSecondayId()) ?: return
                    val defenderAccount = accountCache.getAccount(defenderPlayer.accountKey) ?: return
                    var matchEnd = MatchEndOut(defenderAccount.getChannelWriter(), accountName = accountName,
                            matchResult = MatchResult.LOSS)
                    messageQueue.addMessage(matchEnd)*/
                    val matchEnd = MatchEndOut(account.getChannelWriter(), accountName = accountName,
                            matchResult = MatchResult.WIN)
                    messageQueue.addMessage(matchEnd)
                    matchFinder.removeMatch(match)
                }
            }
            else -> {
                println("Error unable to processIn message:$message")
            }
        }
    }
}