package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.game.asura.card.CardType
import com.game.asura.card.Minion
import com.game.asura.messaging.MessageField

class ClientHeroCard(primaryId: Int,
                     secondaryId: Int,
                     cardCost: Int = 0,
                     cardType: CardType = CardType.HERO) : MonsterDrawableCard(primaryId, secondaryId, cardCost, cardType), Minion {


    private var currentMana: Int = 0
    private var maxMana: Int = 10
    private val playerActor: BoardCard

    init {
        val texture = Texture("core/assets/hero.png")
        playerActor = BoardCard(texture, getSecondayId())
    }



    override fun getActor(): Actor {
        return playerActor
    }



    fun getCurrentMana(): Int {
        return currentMana
    }

    fun getMaxMana(): Int {
        return maxMana
    }



    override fun transformActor(texture: Texture) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}