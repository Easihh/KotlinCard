package com.game.asura

import com.game.asura.messaging.MessageField

class ClientHero(primaryId: Int) : BaseHero(primaryId) {

    private val updateFncMap: MutableMap<MessageField, (ChangedField) -> Unit> = HashMap()

    init {
        updateFncMap[MessageField.PLAYER_CURRENT_HEALTH] = { playerLife = it.value as Int }
        updateFncMap[MessageField.PLAYER_MAX_HEALTH] = { maxPlayerLife = it.value as Int }
        updateFncMap[MessageField.PLAYER_CURRENT_MANA] = { currentPlayerMana = it.value as Int }
        updateFncMap[MessageField.PLAYER_MAX_MANA] = { maxPlayerMana = it.value as Int }
    }

    fun updateField(changedField: ChangedField) {
        updateFncMap[changedField.field]?.invoke(changedField)
    }


}