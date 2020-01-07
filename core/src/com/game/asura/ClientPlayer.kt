package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.game.asura.messaging.MessageField

class ClientPlayer(playerName: String, val heroPower: HeroPower) : Player(playerName) {

    private val updateFncMap: MutableMap<MessageField, (ChangedField) -> Unit> = HashMap()
    val boardManager = BoardManager<DrawableCard>(create = { INVALID_CLIENT_CARD })
    private var currentMatch: Match<ClientPlayer>? = null

    private val playerActor: Image

    init {
        val texture = Texture("core/assets/hero.png")
        playerActor = Image(texture)
    }

    fun setMatch(match: Match<ClientPlayer>) {
        currentMatch = match
    }

    fun getCurrentMatchId(): Int? {
        currentMatch?.let {
            return it.matchId
        }
        return null
    }

    init {
        updateFncMap[MessageField.PLAYER_CURRENT_HEALTH] = { playerLife = it.value as Int }
        updateFncMap[MessageField.PLAYER_MAX_HEALTH] = { maxPlayerLife = it.value as Int }
        updateFncMap[MessageField.PLAYER_CURRENT_MANA] = { currentPlayerMana = it.value as Int }
        updateFncMap[MessageField.PLAYER_MAX_MANA] = { maxPlayerMana = it.value as Int }
    }

    fun update(changes: List<ChangedField>) {
        for (change in changes) {
            updateFncMap[change.field]?.invoke(change)
        }
    }

    fun getActor(): Actor {
        return playerActor
    }
}