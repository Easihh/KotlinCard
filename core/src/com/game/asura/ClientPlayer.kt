package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.game.asura.card.AllCard

class ClientPlayer(val playerName: String, val heroPower: HeroPower) {


    val boardManager = BoardManager<DrawableCard>(create = { INVALID_CLIENT_CARD })
    val handManager = HandManager()
    val heroPlayer = ClientHero(AllCard.MAGE_HERO.id)
    private var currentMatchId: Int? = null

    private val playerActor: Image

    init {
        val texture = Texture("core/assets/hero.png")
        playerActor = Image(texture)
    }

    fun setMatchId(matchId: Int) {
        currentMatchId = matchId
    }

    fun getCurrentMatchId(): Int? {
        return currentMatchId
    }


    fun update(changes: List<ChangedField>) {
        for (change in changes) {
            heroPlayer.updateField(change)
        }
    }

    fun getActor(): Actor {
        return playerActor
    }
}