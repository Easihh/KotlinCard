package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.game.asura.card.CardEffect
import com.game.asura.card.CardType

class MagePower : HeroPower(-1, -1, 2, CardType.TARGET_SPELL) {


    private var img: Image

    init {
        val texture = Texture("core/assets/power.png")
        img = Image(texture)
    }

    override fun initCardTexture(texture: Texture) {

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
}