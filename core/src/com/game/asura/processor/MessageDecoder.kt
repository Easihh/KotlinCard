package com.game.asura.processor

import com.game.asura.InsertableQueue
import com.game.asura.messagein.*
import com.game.asura.messaging.MessageType
import com.game.asura.parsing.CoreMessageParser
import com.game.asura.parsing.DecodedMessage
import com.game.asura.parsing.Tokenizer

class MessageDecoder(private val queue: InsertableQueue) : CoreMessageParser() {

    fun decode(tokenizer: Tokenizer) {
        //pos of 0 due to last compact wont copy anything and after flip nothing remains
        while (tokenizer.hasRemaining() && tokenizer.position() > 0) {
            doDecode(tokenizer)
        }
        tokenizer.clear()
    }

    private fun doDecode(tokenizer: Tokenizer) {
        tokenizer.flip()
        parseMessage(tokenizer)
        var decodedMessage: DecodedMessage? = null
        when (val msgType = getMessageType()) {
            MessageType.CONNECTION_STATE -> {
                decodedMessage = ConnStatusIn()
            }
            MessageType.PLAYER_INFO -> {
                val data = getPlayerInfoData()
                val mana = data.currentMana ?: return
                val maxMana = data.maxMana ?: return
                val name = data.playerName ?: return
                decodedMessage = PlayerInfoIn(name, mana, maxMana)
            }
            MessageType.CARD_DRAWN -> {
                val data = getCardDrawnData()
                val cardCost = data.cardCost ?: return
                val primaryId = data.primaryId ?: return
                val secondaryId = data.secondaryId ?: return
                val cardType = data.cardType ?: return
                decodedMessage = CardDrawnIn(primaryId, secondaryId, cardCost, cardType)
            }
            MessageType.MATCH_INFO -> {
                val data = getMatchInfoData()
                val accountName = data.accountName ?: return
                val enemyName = data.enemyAccountName ?: return
                val primaryId = data.primaryHeroId ?: return
                val secondaryId = data.secondaryHeroId ?: return
                val enemyPrimaryId = data.enemyPrimaryHeroId ?: return
                val enemySecondaryId = data.enemySecondaryHeroId ?: return
                decodedMessage = MatchInfoIn(accountName, enemyName, primaryId, secondaryId, enemyPrimaryId, enemySecondaryId)
            }
            MessageType.CARD_INFO -> {
                val data = getCardInfoData()
                val playerName = data.playerName ?: return
                val primaryId = data.primaryCardId ?: return
                val secondary = data.secondaryCardId ?: return
                decodedMessage = CardInfoIn(playerName, primaryId, secondary, data.playerHealth, data.playerMaxHealth)
            }
            MessageType.CARD_PLAYED -> {
                val data = getCardPlayedData()
                val primaryId = data.cardPrimaryId ?: return
                val secondary = data.cardSecondaryId ?: return
                decodedMessage = CardPlayedIn(primaryId, secondary, data.boardIndex)
            }
            MessageType.HERO_POWER -> {
                val data = getHeroPowerData()
                val target = data.cardTarget ?: return
                decodedMessage = HeroPowerIn(target)
            }
            MessageType.START_TURN -> {
                val data = getStartTurnData()
                decodedMessage = StartTurnIn()
            }
            MessageType.END_TURN -> {
                val data = getEndTurnData()
                decodedMessage = EndTurnIn()
            }
            else -> {
                println("Message of type $msgType has no decode logic.")
            }
        }
        if (decodedMessage == null) {
            println("Cannot process Decoded Message since it is null.")
            return
        }
        queue.addMessage(decodedMessage)
        tokenizer.compact()
    }
}