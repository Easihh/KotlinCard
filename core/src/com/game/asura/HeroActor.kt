package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image

data class HeroActor(private val texture: Texture?,
                val secondaryId: Int) : Image(texture)