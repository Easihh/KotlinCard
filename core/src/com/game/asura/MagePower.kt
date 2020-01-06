package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.game.asura.card.CardEffect
import com.game.asura.card.CardType

class MagePower : HeroPower {

    private var img: Image

    init {
        val texture = Texture("core/assets/power.png")
        img = Image(texture)
    }

    private var isActive: Boolean = false

    override fun getActor(): Actor {
        return img
    }

    override fun transformActor(texture: Texture) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isActive(): Boolean {
        return isActive
    }

    override fun activate() {
        isActive = true
    }

    override fun deactivate() {
        isActive = false
    }

    override fun getPrimaryId(): Int {
        return -1
    }

    override fun getSecondayId(): Int {
        return -1
    }

    override fun getCost(): Int {
        return 2
    }

    override fun getCardType(): CardType {
        return CardType.TARGET_SPELL
    }

    override fun getEffect(): List<CardEffect> {
        return emptyList()
    }

    override fun getAttack(): Int? {
        return null
    }

    override fun getHealth(): Int? {
        return null
    }
}