package com.game.asura

import com.game.asura.card.CardType
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class EnumMapper {

    private val enumMap: MutableMap<MessageField, (Byte) -> String> = HashMap()
    private val enumMapInt: MutableMap<MessageField, (Int) -> String?> = HashMap()

    init {
        val messageTypeFnc: (Byte) -> String = {
            MessageType.getMessageType(it).name
        }
        val loginStatusFnc: (Byte) -> String = {
            LoginStatus.getLoginStatus(it).name
        }
        val gameTypeFnc: (Byte) -> String = {
            GameType.getGameType(it).name
        }
        val matchResultTypeFnc: (Byte) -> String = {
            MatchResult.getMatchResultType(it).name
        }
        val cardTypeFnc: (Byte) -> String = {
            CardType.getCardType(it).name
        }
        val phaseTypeFnc: (Byte) -> String = {
            Phase.getPhase(it).name
        }

        enumMap.putIfAbsent(MessageField.MESSAGE_TYPE, messageTypeFnc)
        enumMap.putIfAbsent(MessageField.LOGIN_STATUS, loginStatusFnc)
        enumMap.putIfAbsent(MessageField.GAME_TYPE, gameTypeFnc)
        enumMap.putIfAbsent(MessageField.CARD_TYPE, cardTypeFnc)
        enumMap.putIfAbsent(MessageField.NEXT_PHASE, phaseTypeFnc)
        enumMap.putIfAbsent(MessageField.MATCH_RESULT, matchResultTypeFnc)
    }

    fun getFieldValueName(field: MessageField, value: Byte): String? {
        return enumMap[field]?.invoke(value)
    }

    fun getFieldValueName(field: MessageField, id: Int): String? {
        return enumMapInt[field]?.invoke(id)
    }
}