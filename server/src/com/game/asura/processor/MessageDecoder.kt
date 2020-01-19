package com.game.asura.processor

import com.game.asura.*
import com.game.asura.account.Account
import com.game.asura.card.CardInfoStore
import com.game.asura.messagein.*
import com.game.asura.messaging.MessageType
import com.game.asura.parsing.CoreMessageParser
import com.game.asura.parsing.DecodedMessage

class MessageDecoder(private val queue: InsertableQueue) : CoreMessageParser() {

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
                decodedMessage = CardPlayedIn(playerAccount.getAccountKey(), cardId, secCardId,
                        cardPlayedData.cardTarget, cardPlayedData.boardIndex)
            }
            MessageType.HERO_POWER -> {
                val heroPowerdata = getHeroPowerData()
                decodedMessage = HeroPowerIn(playerAccount.getAccountKey(), heroPowerdata.cardTarget)

            }
            MessageType.END_TURN -> {
                val endTurnData = getEndTurnData()
                decodedMessage = EndTurnIn(playerAccount.getAccountKey())
            }
            MessageType.MONSTER_ATTACK -> {
                val monsterAttackData = getMonsterAttackData()
                val primaryId = monsterAttackData.primaryId ?: return
                val secondaryId = monsterAttackData.secondaryId ?: return
                val cardTarget = monsterAttackData.cardTarget ?: return
                decodedMessage = MonsterAttackIn(playerAccount.getAccountKey(), primaryId, secondaryId, cardTarget)
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