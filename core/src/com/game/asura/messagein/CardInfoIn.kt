package com.game.asura.messagein

import com.game.asura.ChangedField
import com.game.asura.parsing.DecodedMessage
import com.game.asura.messaging.MessageField

class CardInfoIn(playerName: String,
                 playerHealth: Int?,
                 playerMaxHealth: Int?) : DecodedMessage {
    //List of fields we received
    private val infoFieldsLst: MutableList<ChangedField> = ArrayList()

    init {
        addField(MessageField.ACCOUNT_NAME, playerName)
        addField(MessageField.CARD_HEALTH, playerHealth)
        addField(MessageField.CARD_MAX_HEALTH, playerMaxHealth)
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