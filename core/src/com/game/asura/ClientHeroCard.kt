package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.game.asura.card.CardType
import com.game.asura.card.Minion

class ClientHeroCard(primaryId: Int,
                     secondaryId: Int,
                     cardCost: Int = 0,
                     cardType: CardType = CardType.HERO,
                     attack: Int? = null,
                     health: Int,
                     maxHealth: Int) : MonsterDrawableCard(primaryId, secondaryId, cardCost, cardType, attack, health, maxHealth), Minion {


    private lateinit var playerActor: BoardCard

    override fun initCardTexture(texture: Texture) {
        val texture = Texture("core/assets/hero.png")
        playerActor = BoardCard(texture, getSecondayId())
    }

    override fun getActor(): Actor {
        return playerActor
    }

    override fun transformActor(texture: Texture) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}