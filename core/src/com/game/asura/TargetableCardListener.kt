package com.game.asura

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable

class TargetableCardListener(private val card: DrawableCard,
                             private val initTargetSpellFnc: (DrawableCard, Position) -> Unit) : InputListener() {

    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        println("Selected Card:$card")
        if (button == Input.Buttons.LEFT) {
            //so we don't try and target our own card
            card.actor.touchable = Touchable.disabled
            initTargetSpellFnc(card, Position(event.stageX, event.stageY))
        }
        return true
    }
}