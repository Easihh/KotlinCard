package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Image
//need keep track of secondary id for actor to determine which card it is e.g when targeting
data class CardActor(private val texture: Texture?,
                     val secondaryId: Int) : Image(texture)