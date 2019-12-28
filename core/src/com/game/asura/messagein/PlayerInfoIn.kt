package com.game.asura.messagein

import com.game.asura.ChangedField
import com.game.asura.DecodedMessage
import com.game.asura.MessageField

class PlayerInfoIn(playerName: String,
                   playerHealth: Int?,
                   playerMaxHealth: Int?,
                   playerMana: Int?,
                   playerMaxMana: Int?) : DecodedMessage {
    //List of fields we received
    private val infoFieldsLst: MutableList<ChangedField> = ArrayList()

    init {
        addField(MessageField.ACCOUNT_NAME, playerName)
        addField(MessageField.PLAYER_CURRENT_HEALTH, playerHealth)
        addField(MessageField.PLAYER_MAX_HEALTH, playerMaxHealth)
        addField(MessageField.PLAYER_CURRENT_MANA, playerMana)
        addField(MessageField.PLAYER_MAX_MANA, playerMaxMana)
    }

    private fun addField(field: MessageField, value: Any?) {
        if (value == null) {
            println("Null Value on field:${field.name} disregarding it.")
            return
        }
        infoFieldsLst.add(ChangedField(field, value))
    }

    fun getChangedFields(): List<ChangedField> {
        return infoFieldsLst
    }
}