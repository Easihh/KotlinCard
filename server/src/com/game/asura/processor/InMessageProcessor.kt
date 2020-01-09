package com.game.asura.processor

import com.game.asura.*
import com.game.asura.account.CachedAccount
import com.game.asura.account.PlayerAccount
import com.game.asura.card.AllCard
import com.game.asura.card.CardEffect
import com.game.asura.messagein.*
import com.game.asura.messageout.*
import com.game.asura.messaging.MessageField
import com.game.asura.parsing.DecodedMessage

class InMessageProcessor(private val messageQueue: InsertableQueue,
                         private val accountCache: CachedAccount,
                         private val matchFinder: MatchFinder) {

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
                val player = ServerPlayer(accountName)
                player.initializeDeck()
                val match = Match<ServerPlayer>()
                match.addPlayer(accountName, player)
                matchFinder.addMatch(match)
                //send MatchId to concerned players
                val matchInfo = MatchInfoOut(account.getChannelWriter(), matchId = match.matchId)
                messageQueue.addMessage(matchInfo)
                //send start turn to a player
                val startTurn = StartTurnOut(account.getChannelWriter(), matchId = match.matchId)
                messageQueue.addMessage(startTurn)
                //schedule end turn X seconds from now or until we receive such info player client.
                val endTurn = EndTurnIn(account.getAccountKey(), matchId = match.matchId, matchTurn = match.getMatchTurn())
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
                val match = matchFinder.findMatch(message.matchId)
                if (match == null) {
                    println("Unable to find match with id:${message.matchId}.")
                    return
                }
                val account = accountCache.getAccount(message.accountKey)
                val accountName = account?.getAccountName()
                if (accountName == null) {
                    println("Unable to find accountName in cache with key:${message.accountKey}.")
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
                //validation is finish, can now play card so send back message to all player
                val cardPlayed = CardPlayedOut(account.getChannelWriter(), accountName, cardInHand, message.cardTarget, message.boardPosition)
                messageQueue.addMessage(cardPlayed)

                player.playCard(cardInHand)
                val changedFields: MutableList<ChangedField> = ArrayList()
                if (cardInHand.getCost() > 0) {
                    val manaField = ChangedField(MessageField.PLAYER_CURRENT_MANA, player.getPlayerMana())
                    changedFields.add(manaField)
                }
                val effects = cardInHand.getEffect()
                for (effect in effects) {
                    if (effect == CardEffect.DEAL_DMG) {
                        if (message.cardTarget == null) {
                            val cardName = AllCard.getCard(message.cardPrimaryId)
                            println("Error, card:$cardName from player:$accountName was played no target.")
                            break
                        }
                        //targeting  own minion [0-6] or yourself (7)
                        if (message.cardTarget == 7) {
                            player.takeDmg(3)
                            val healthField = ChangedField(MessageField.PLAYER_CURRENT_HEALTH, player.getCurrentPlayerLife())
                            changedFields.add(healthField)
                            val playerInfoOut = PlayerInfoOut(channelWriter = account.getChannelWriter(), accoutName = accountName, changedFields = changedFields)
                            messageQueue.addMessage(playerInfoOut)
                            continue
                        }
                        if (message.cardTarget < 7) {
                            continue
                        }
                        //targeting enemy minion [8-15] or enemy itself (15)
                    }
                }
                println("Handle card played on server here.")
            }
            is HeroPowerIn -> {
                val match = matchFinder.findMatch(message.matchId)
                if (match == null) {
                    println("Unable to find match with id:${message.matchId}.")
                    return
                }
                val account = accountCache.getAccount(message.accountKey)
                val accountName = account?.getAccountName()
                if (accountName == null) {
                    println("Unable to find accountName in cache with key:${message.accountKey}.")
                    return
                }
                println("Handle Hero Power played on server here.")

                //send back to concerned players
                val heroPowerOut = HeroPowerOut(channelWriter = account.getChannelWriter(), accountName = accountName, target = message.target)
                messageQueue.addMessage(heroPowerOut)
                //should also send update for mana usage for example
            }

            is EndTurnIn -> {
                if (message.matchTurn != null) {
                    //server is end the turn due to time being over limit

                    //look if player already had ended turn
                    val match = matchFinder.findMatch(message.matchId) ?: return
                    if (message.matchTurn < match.getMatchTurn()) {
                        //player has already send end turn before timer up, disregard this internal end turn msg.
                        println("Disregarding end turn timeout scheduled since player sent end turn already for match:${message.matchId} turn:${message.matchTurn}")
                        return
                    }
                    //force time out as we did not receive an end turn message in time
                    match.increaseMatchTurn()
                    val account = accountCache.getAccount(message.accountKey)
                    val accountName = account?.getAccountName()
                    if (accountName == null) {
                        println("Unable to find accountName in cache with key:${message.accountKey}.")
                        return
                    }

                    val endTurnOut = EndTurnOut(account.getChannelWriter(), message.matchId)
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
                val match = matchFinder.findMatch(message.matchId) ?: return
                match.monsterAttack(message.secondaryId, message.target)
            }
            else -> {
                println("Error unable to processIn message:$message")
            }
        }
    }
}