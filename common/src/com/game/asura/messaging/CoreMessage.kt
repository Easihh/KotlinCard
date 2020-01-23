package com.game.asura.messaging

import com.game.asura.GameType
import com.game.asura.LoginStatus
import com.game.asura.MatchResult
import com.game.asura.card.CardType

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

    fun getEnemyAccountName(): String? {
        val typeVal = fieldMap[MessageField.ENEMY_ACCOUNT_NAME] ?: return null
        return typeVal.value as String
    }

    fun getCardHealth(): Int? {
        val typeVal = fieldMap[MessageField.CARD_HEALTH] ?: return null
        return typeVal.value as Int
    }

    fun getCurrentPlayerMana(): Int? {
        val typeVal = fieldMap[MessageField.PLAYER_CURRENT_MANA] ?: return null
        return typeVal.value as Int
    }

    fun getMaxCardLife(): Int? {
        val typeVal = fieldMap[MessageField.CARD_MAX_HEALTH] ?: return null
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

    fun getPrimaryHeroId(): Int? {
        val typeVal = fieldMap[MessageField.HERO_PRIMARY_ID] ?: return null
        return typeVal.value as Int
    }

    fun getEnemyPrimaryHeroId(): Int? {
        val typeVal = fieldMap[MessageField.ENEMY_HERO_PRIMARY_ID] ?: return null
        return typeVal.value as Int
    }

    fun getSecondaryCardId(): Int? {
        val typeVal = fieldMap[MessageField.SECONDARY_CARD_ID] ?: return null
        return typeVal.value as Int
    }

    fun getSecondaryHeroId(): Int? {
        val typeVal = fieldMap[MessageField.HERO_SECONDARY_ID] ?: return null
        return typeVal.value as Int
    }

    fun getEnemySecondaryHeroId(): Int? {
        val typeVal = fieldMap[MessageField.ENEMY_HERO_SECONDARY_ID] ?: return null
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

    fun getBoardPosition(): Int? {
        val typeVal = fieldMap[MessageField.BOARD_POSITION] ?: return null
        return typeVal.value as Int
    }

    fun getMatchTurn(): Int? {
        val typeVal = fieldMap[MessageField.MATCH_TURN] ?: return null
        return typeVal.value as Int
    }

    fun getMatchResult(): MatchResult? {
        val typeVal = fieldMap[MessageField.MATCH_RESULT] ?: return null
        return MatchResult.getMatchResultType(typeVal.value as Byte)
    }

    fun getCardAttack(): Int? {
        val typeVal = fieldMap[MessageField.CARD_ATTACK] ?: return null
        return typeVal.value as Int
    }

    fun getFirstMonsterId(): Int? {
        val typeVal = fieldMap[MessageField.FIRST_MONSTER_ID] ?: return null
        return typeVal.value as Int
    }

    fun getSecondMonsterId(): Int? {
        val typeVal = fieldMap[MessageField.SECOND_MONSTER_ID] ?: return null
        return typeVal.value as Int
    }

    fun getLoginStatus(): LoginStatus? {
        val typeVal = fieldMap[MessageField.LOGIN_STATUS] ?: return null
        return LoginStatus.getLoginStatus(typeVal.value as Byte)
    }


    fun clear() {
        fieldMap.clear()
    }

}