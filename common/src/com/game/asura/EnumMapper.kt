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
        val connStatusFnc: (Byte) -> String = {
            ConnectionStatus.getConnectionStatus(it).name
        }
        val gameTypeFnc: (Byte) -> String = {
            GameType.getGameType(it).name
        }
        val cardTypeFnc: (Byte) -> String = {
            CardType.getCardType(it).name
        }

        enumMap.putIfAbsent(MessageField.MESSAGE_TYPE, messageTypeFnc)
        enumMap.putIfAbsent(MessageField.CONN_STATUS, connStatusFnc)
        enumMap.putIfAbsent(MessageField.GAME_TYPE, gameTypeFnc)
        enumMap.putIfAbsent(MessageField.CARD_TYPE, cardTypeFnc)
        //enumMapInt.putIfAbsent(MessageField.PRIMARY_CARD_ID, cardNameFnc)
    }

    fun getFieldValueName(field: MessageField, value: Byte): String? {
        return enumMap[field]?.invoke(value)
    }

    fun getFieldValueName(field: MessageField, id: Int): String? {
        return enumMapInt[field]?.invoke(id)
    }
}