package com.game.asura.processor

import com.game.asura.*
import com.game.asura.account.Account
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
                val matchId = cardPlayedData.matchId ?: return
                decodedMessage = CardPlayedIn(playerAccount.getAccountKey(), cardId, secCardId, matchId,
                        cardPlayedData.cardTarget, cardPlayedData.boardIndex)
            }
            MessageType.HERO_POWER -> {
                val heroPowerdata = getHeroPowerData()
                val matchId = heroPowerdata.matchId ?: return
                decodedMessage = HeroPowerIn(playerAccount.getAccountKey(), matchId, heroPowerdata.cardTarget)

            }
            MessageType.END_TURN -> {
                val endTurnData = getEndTurnData()
                val matchId = endTurnData.matchId ?: return
                decodedMessage = EndTurnIn(playerAccount.getAccountKey(), matchId)
            }
            MessageType.MONSTER_ATTACK -> {
                val monsterAttackData = getMonsterAttackData()
                val matchId = monsterAttackData.matchId ?: return
                val primaryId = monsterAttackData.primaryId ?: return
                val secondaryId = monsterAttackData.secondaryId ?: return
                val cardTarget = monsterAttackData.cardTarget ?: return
                decodedMessage = MonsterAttackIn(playerAccount.getAccountKey(), matchId, primaryId, secondaryId, cardTarget)
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