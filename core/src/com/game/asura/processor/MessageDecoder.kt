package com.game.asura.processor

import com.game.asura.*
import com.game.asura.messagein.*

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
            MessageType.CARD_DRAWN -> {
                val data = getCardDrawnData()
                val cardCost = data.cardCost ?: return
                val primaryId = data.primaryId ?: return
                val secondaryId = data.secondaryId ?: return
                val cardType = data.cardType ?: return
                decodedMessage = CardDrawnIn(primaryId, secondaryId, cardCost, cardType)
            }
            MessageType.MATCH_INFO -> {
                val matchInfoData = getMatchInfoData()
                val matchId = matchInfoData.matchId ?: return
                decodedMessage = MatchInfoIn(matchId, matchInfoData.gameType)
            }
            MessageType.PLAYER_INFO -> {
                val data = getPlayerInfoData()
                val playerName = data.playerName ?: return
                decodedMessage = PlayerInfoIn(playerName, data.playerHealth, data.playerMaxHealth, data.playerMana, data.playerMaxMana)
            }
            MessageType.CARD_PLAYED -> {
                val data = getCardPlayedData()
                val primaryId = data.cardPrimaryId ?: return
                val secondary = data.cardSecondaryId ?: return
                decodedMessage = CardPlayedIn(primaryId, secondary, data.boardIndex)
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