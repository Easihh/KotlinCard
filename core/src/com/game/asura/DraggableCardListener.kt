package com.game.asura

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.DragListener

class DraggableCardListener(private val card: DrawableCard,
                            private val initSelectCardFnc: (DrawableCard, Position) -> Unit,
                            private val hasCardSelectFnc: () -> Boolean) : DragListener() {

    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
            println("Selected Card:$card")
            initSelectCardFnc(card, Position(event.stageX, event.stageY))
        }
        return true
    }

    override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
        //since it's possible to cancel card moving but still hold drag, we don't want to keep
        //moving the actor since the selection is gone
        if (hasCardSelectFnc()) {
            card.getActor().moveBy(x - card.getActor().width / 2, y - card.getActor().height / 2)
        }
    }
}