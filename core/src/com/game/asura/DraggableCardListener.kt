package com.game.asura

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.DragListener

class DraggableCardListener(private val card: DrawableCard,
                            private val playCardFnc: (DrawableCard, Position) -> Unit) : DragListener() {

    override fun drag(event: InputEvent, x: Float, y: Float, pointer: Int) {
        card.getActor().moveBy(x - card.getActor().width / 2, y - card.getActor().height / 2)
    }

    override fun dragStop(event: InputEvent, x: Float, y: Float, pointer: Int) {
        card.getActor().removeListener(this)
        playCardFnc(card, Position(event.stageX, event.stageY))
    }
}