package com.game.asura

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.DragListener

class DraggableCardListener(private val card: DrawableCard,
                            private val initSelectCardFnc: (DrawableCard) -> Unit,
                            private val playCardFnc: (DrawableCard, Position) -> Unit) : DragListener() {

    private var cardIsDragged = false
    private var cardIsSelected = false
    override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
        if (cardIsDragged || cardIsSelected) {
            playCardFnc(card, Position(event.stageX, event.stageY))
            cardIsDragged = false
            cardIsSelected = false
            return
        }
        if (!cardIsSelected) {
            println("Selected Card:$card")
            //so we don't try and target our own card
            card.getActor().touchable = Touchable.disabled
            initSelectCardFnc(card)
            cardIsSelected = true
        }
    }

    override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        return true
    }

    override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {
        card.getActor().moveBy(x - card.getActor().width / 2, y - card.getActor().height / 2);
        if (!cardIsDragged && !cardIsSelected) {
            println("Selected Card:$card")
            cardIsDragged = true
            //so we don't try and target our own card
            card.getActor().touchable = Touchable.disabled
            initSelectCardFnc(card)
            cardIsSelected = true
        }
    }
}