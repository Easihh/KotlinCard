package com.game.asura

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.DragListener

class NonDraggableCardListener(private val card: DrawableCard,
                               private val initTargetSpellFnc: (DrawableCard, Position) -> Unit) : DragListener() {

    override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int) {
        println("Call playCard.")
        //so we don't try and target our own card
        card.getActor().touchable = Touchable.disabled

        initTargetSpellFnc(card, Position(event.stageX, event.stageY))
    }
}