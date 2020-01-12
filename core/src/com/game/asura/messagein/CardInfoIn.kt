package com.game.asura.messagein

import com.game.asura.ChangedField
import com.game.asura.messaging.MessageField
import com.game.asura.parsing.DecodedMessage

class CardInfoIn(val playerName: String,
                 val primaryCardId: Int,
                 val secondaryCardId: Int,
                 playerHealth: Int?,
                 playerMaxHealth: Int?) : DecodedMessage {
    //List of fields we received
    private val infoFieldsLst: MutableList<ChangedField> = ArrayList()

    init {
        addField(MessageField.CARD_HEALTH, playerHealth)
        addField(MessageField.CARD_MAX_HEALTH, playerMaxHealth)
        addField(MessageField.PLAYER_CURRENT_MANA, playerHealth)
        addField(MessageField.PLAYER_MAX_MANA, playerMaxHealth)
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