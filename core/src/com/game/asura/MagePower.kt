package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image

class MagePower : HeroPower {

    private var img: Image

    init {
        val texture = Texture("core/assets/power.png")
        img = Image(texture)
    }

    private var isActive: Boolean = false

    override fun getImage(): Image {
        return img
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