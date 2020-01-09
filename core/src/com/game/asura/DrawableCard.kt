package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.game.asura.card.Card

interface DrawableCard : Card {

    fun getActor(): Actor

    fun transformActor(texture: Texture)

    fun getAttack(): Int?

    fun getHealth(): Int?
}