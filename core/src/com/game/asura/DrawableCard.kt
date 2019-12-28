package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image

interface DrawableCard : Card {

    fun getImage(): Image

    fun getTexture(): Texture
}