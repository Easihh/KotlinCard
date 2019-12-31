package com.game.asura

import com.badlogic.gdx.scenes.scene2d.Actor

interface DrawableCard : Card {

    fun getActor(): Actor

}