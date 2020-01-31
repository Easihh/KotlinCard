package com.game.asura.processor

import com.game.asura.CardInfoStore
import com.game.asura.InsertableQueue
import com.game.asura.account.Account
import com.game.asura.card.CardType
import com.game.asura.messagein.*
import com.game.asura.messaging.MessageType
import com.game.asura.parsing.CoreMessageParser
import com.game.asura.parsing.DecodedMessage

class MessageDecoder(private val queue: InsertableQueue,
                     private val cardInfoStore: CardInfoStore) : CoreMessageParser() {

    fun decode(playerAccount: Account) {
        val tokenizer = playerAccount.getTokenizer()
        parseMessage(tokenizer)
        var decodedMessage: DecodedMessage? = null
        when (val msgType = getMessageType()) {
            MessageType.LOGIN_REQUEST -> {
                decodedMessage = LoginRequestIn(playerAccount)
            }
            MessageType.GAME_REQUEST -> {
                val gameRequestData = getGameRequestData()
                decodedMessage = GameRequestIn(playerAccount.getAccountKey(), gameRequestData.gameType)
            }
            MessageType.CARD_PLAYED -> {
                val cardPlayedData = getCardPlayedData()
                //should validate important field here
                val cardId = cardPlayedData.cardPrimaryId ?: return
                val secCardId = cardPlayedData.cardSecondaryId ?: return
                when (val cardType = cardInfoStore.getCardInfo(cardId)?.cardType ?: return) {
                    CardType.MONSTER -> {
                        val boardIndx = cardPlayedData.boardIdx ?: return
                        decodedMessage = MonsterCardPlayedIn(playerAccount.getAccountKey(), cardId, secCardId, boardIndx)
                    }
                    CardType.SPELL -> {
                        decodedMessage = SpellCardPlayedIn(playerAccount.getAccountKey(), cardId, secCardId,
                                cardPlayedData.cardTarget)
                    }
                    else -> {
                    }
                }
            }
            MessageType.END_TURN -> {
                val endTurnData = getEndTurnData()
                decodedMessage = EndTurnIn(playerAccount.getAccountKey())
            }
            MessageType.ATTACK -> {
                decodedMessage = AttackIn(playerAccount.getAccountKey())
            }
            else -> {
                println("Message of type $msgType has no decode logic.")
            }
        }
        tokenizer.clear()
        if (decodedMessage == null) {
            println("Cannot process Decoded Message since it is null.")
            return
        }
        queue.addMessage(decodedMessage)
    }
}