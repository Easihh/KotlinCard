package com.game.asura

import com.badlogic.gdx.scenes.scene2d.ui.Image

interface HeroPower {

    fun getImage(): Image

    fun isActive(): Boolean

    fun activate()

    fun deactivate()
}