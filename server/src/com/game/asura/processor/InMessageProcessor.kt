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
                val player = ServerPlayer(accountName, AllCard.MAGE_HERO.id)
                val enemyPlayer = ServerPlayer("Enemy", AllCard.MAGE_HERO.id)
                player.initializeDeck()
                enemyPlayer.initializeDeck()
                val match = Match()
                account.setMatch(match)
                match.addPlayer(accountName, player)
                match.addPlayer("Enemy", enemyPlayer)
                matchFinder.addMatch(match)
                //send MatchId to concerned players
                //1st player
                val matchInfo = MatchInfoOut(account.getChannelWriter(), accountName, "Enemy", player.heroPlayer, enemyPlayer.heroPlayer)
                //2nd player
                //val matchInfo2 = MatchInfoOut(account.getChannelWriter(), "Enemy", accountName, enemyPlayer.heroPlayer, player.heroPlayer)
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
                //validation is finish, can now play card so send back message to all player
                val cardPlayed = CardPlayedOut(account.getChannelWriter(), accountName, cardInHand, message.cardTarget, message.boardPosition)
                messageQueue.addMessage(cardPlayed)

                player.playCard(cardInHand)
                val changedFields: MutableList<ChangedField> = ArrayList()
                if (cardInHand.getCost() > 0) {
                    val manaField = ChangedField(MessageField.PLAYER_CURRENT_MANA, player.heroPlayer.getCurrentMana())
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
                            player.heroPlayer.takeDmg(3)
                            val healthField = ChangedField(MessageField.PLAYER_CURRENT_HEALTH, player.heroPlayer.getHealth())
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
                println("Handle Hero Power played on server here.")

                //send back to concerned players
                val heroPowerOut = HeroPowerOut(channelWriter = account.getChannelWriter(), accountName = accountName, target = message.target)
                messageQueue.addMessage(heroPowerOut)
                //should also send update for mana usage for example
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
                match.monsterAttack(message.secondaryId, message.target)
            }
            else -> {
                println("Error unable to processIn message:$message")
            }
        }
    }
}