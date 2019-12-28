package com.game.asura.processor

import com.game.asura.*
import com.game.asura.account.CachedAccount
import com.game.asura.account.PlayerAccount
import com.game.asura.messagein.CardPlayedIn
import com.game.asura.messagein.GameRequestIn
import com.game.asura.messagein.LoginRequestIn
import com.game.asura.messageout.CardDrawnOut
import com.game.asura.messageout.CardPlayedOut
import com.game.asura.messageout.MatchInfoOut
import com.game.asura.messageout.PlayerInfoOut

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
                val match = Match<ServerPlayer>(gameType = message.gameType)
                match.addPlayer(accountName, player)
                matchFinder.addMatch(match)
                //send MatchId to concerned players
                val matchInfo = MatchInfoOut(account.getChannelWriter(), matchId = match.matchId)
                messageQueue.addMessage(matchInfo)
                //send initial draws
                for (x in 0..3) {
                    val cardDrawn = player.draw() ?: return
                    val cardDrawnOut = CardDrawnOut(account.getChannelWriter(), cardDrawn, player.cardRemaining())
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
                val cardInHand = player.getCardFromHand(message.cardSecondaryId)
                if (cardInHand == null) {
                    println("Unable to find card with secondaryId:${message.cardSecondaryId} in player:$accountName hand.")
                    return
                }
                //validation is finish, can now play card so send back message to all player
                val cardPlayed = CardPlayedOut(account.getChannelWriter(), accountName, cardInHand, message.cardTarget,message.boardPosition)
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
        }
    }
}