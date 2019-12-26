package com.game.asura

class CoreMessage {

    private data class FieldTypeValue(val type: Char,
                                      val value: Any)

    private val fieldMap: MutableMap<MessageField, FieldTypeValue> = HashMap()

    fun addField(key: MessageField, type: Char, value: Any) {
        val myVal = FieldTypeValue(type, value)
        fieldMap.putIfAbsent(key, myVal)
    }

    fun getMessageType(): MessageType {
        val typeVal = fieldMap[MessageField.MESSAGE_TYPE] ?: return MessageType.UNKNOWN
        return MessageType.getMessageType(typeVal.value as Byte)
    }

    fun getGameType(): GameType {
        val typeVal = fieldMap[MessageField.GAME_TYPE] ?: return GameType.UNKNOWN
        return GameType.getGameType(typeVal.value as Byte)
    }

    fun getAccountName(): String? {
        val typeVal = fieldMap[MessageField.ACCOUNT_NAME] ?: return null
        return typeVal.value as String
    }

    fun getCurrentPlayerLife(): Int? {
        val typeVal = fieldMap[MessageField.PLAYER_CURRENT_HEALTH] ?: return null
        return typeVal.value as Int
    }

    fun getCurrentPlayerMana(): Int? {
        val typeVal = fieldMap[MessageField.PLAYER_CURRENT_MANA] ?: return null
        return typeVal.value as Int
    }

    fun getMaxPlayerLife(): Int? {
        val typeVal = fieldMap[MessageField.PLAYER_MAX_HEALTH] ?: return null
        return typeVal.value as Int
    }

    fun getMaxPlayerMana(): Int? {
        val typeVal = fieldMap[MessageField.PLAYER_MAX_MANA] ?: return null
        return typeVal.value as Int
    }

    fun getCardCost(): Int? {
        val typeVal = fieldMap[MessageField.CARD_COST] ?: return null
        return typeVal.value as Int
    }

    fun getPrimaryCardId(): Int? {
        val typeVal = fieldMap[MessageField.PRIMARY_CARD_ID] ?: return null
        return typeVal.value as Int
    }

    fun getSecondaryCardId(): Int? {
        val typeVal = fieldMap[MessageField.SECONDARY_CARD_ID] ?: return null
        return typeVal.value as Int
    }

    fun getCardPrimaryId(): Int? {
        val typeVal = fieldMap[MessageField.PRIMARY_CARD_ID] ?: return null
        return typeVal.value as Int
    }

    fun getMatchId(): Int? {
        val typeVal = fieldMap[MessageField.MATCH_ID] ?: return null
        return typeVal.value as Int
    }

    fun getCardTarget(): Int? {
        val typeVal = fieldMap[MessageField.CARD_TARGET] ?: return null
        return typeVal.value as Int
    }

    fun getCardType(): CardType? {
        val typeVal = fieldMap[MessageField.CARD_TYPE] ?: return null
        return CardType.getCardType(typeVal.value as Byte)
    }

    fun clear() {
        fieldMap.clear()
    }

}