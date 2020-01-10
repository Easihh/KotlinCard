package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.game.asura.messaging.MessageField

class ClientHero(primaryId: Int) : BaseHero(primaryId) {

    private val updateFncMap: MutableMap<MessageField, (ChangedField) -> Unit> = HashMap()
    private val playerActor: BoardCard

    init {

        val texture = Texture("core/assets/hero.png")
        playerActor = BoardCard(texture, secondary)

        updateFncMap[MessageField.PLAYER_CURRENT_HEALTH] = { playerLife = it.value as Int }
        updateFncMap[MessageField.PLAYER_MAX_HEALTH] = { maxPlayerLife = it.value as Int }
        updateFncMap[MessageField.PLAYER_CURRENT_MANA] = { currentPlayerMana = it.value as Int }
        updateFncMap[MessageField.PLAYER_MAX_MANA] = { maxPlayerMana = it.value as Int }
    }

    fun updateField(changedField: ChangedField) {
        updateFncMap[changedField.field]?.invoke(changedField)
    }

    fun getActor(): Actor {
        return playerActor
    }
}