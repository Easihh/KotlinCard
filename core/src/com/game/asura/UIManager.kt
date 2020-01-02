package com.game.asura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.game.asura.messageout.CardPlayedOut
import com.game.asura.messageout.HeroPowerOut

class UIManager(private val stage: Stage,
                private val queue: InsertableQueue,
                private val player: ClientPlayer) {

    private var mouseX: Float = 0f
    private var mouseY: Float = 0f
    private var initialClickX: Float = 0f
    private var initialClickY: Float = 0f
    private val cardTargeted = Texture("core/assets/cardTargeted.png")
    private val arrowPointer = Texture("core/assets/arrow.png")
    private val arrowImg = Sprite(arrowPointer)
    private val targetCircle = Sprite(Texture("core/assets/target.png"))
    private val systemCursor = Cursor.SystemCursor.Hand
    private val invisibleCursor = Pixmap(Gdx.files.internal("core/assets/invisibleCursor.png"))
    private val cursor = Gdx.graphics.newCursor(invisibleCursor, 0, 0)
    private val healthHeroImg = Texture("core/assets/health.png")
    private var sourceTargetCard: DrawableCard? = null

    init {
        cursor.dispose()
        setupHeroPower()
        addHeroToBoard()
        val mouseMovedLstr = object : InputListener() {
            override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
                if (event == null) {
                    return false
                }
                mouseX = event.stageX
                mouseY = event.stageY

                arrowImg.setPosition(mouseX + 16f, mouseY - 16f)
                return false
            }
        }
        stage.addListener(mouseMovedLstr)
        setupClickListener()
    }

    private fun addHeroToBoard() {
        val actor = player.getActor()
        actor.setPosition(500f, 50f)
        stage.addActor(actor)
    }

    private fun setupClickListener() {
        stage.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (event == null) {
                    return false
                }
                val matchId = player.getCurrentMatchId()
                if (matchId == null) {
                    println("Error, current match is null.")
                    return false
                }
                if (button == Input.Buttons.LEFT) {
                    sourceTargetCard?.let {
                        playTargetSpell(it, Position(event.stageX, event.stageY), matchId)
                    }
                    return false
                }
                if (button == Input.Buttons.RIGHT) {
                    println("Right Clicked.")
                    if (sourceTargetCard != null) {
                        println("clearing target action for card:$sourceTargetCard")
                        clearTargetingAction()
                        return false
                    }
                }
                return false
            }
        })
    }

    private fun clearTargetingAction() {
        //allow hero power/card to trigger listener again
        sourceTargetCard?.let {
            it.getActor().touchable = Touchable.enabled
        }
        sourceTargetCard = null
        //reset to normal cursor here
        Gdx.graphics.setSystemCursor(systemCursor)
    }


    private fun setupHeroPower() {
        val heroPower = player.heroPower.getActor()
        heroPower.setPosition(675f, 50f)
        val intputlstr = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (event == null) {
                    return false
                }
                println("Hero Power click triggered.")
                Gdx.graphics.setCursor(cursor)

                initialClickX = event.stageX
                initialClickY = event.stageY
                sourceTargetCard = player.heroPower

                //so that another click on hero power won't be triggered by stage hit until target action is canceled
                heroPower.touchable = Touchable.disabled
                return false
            }
        }
        heroPower.addListener(intputlstr)
        stage.addActor(heroPower)
    }

    private fun getClosestEmptyBoardIndex(mouseX: Float, mouseY: Float): Int? {
        val boardManager = player.boardManager
        if (boardManager.boardIsEmpty()) {
            return (MAX_BOARD_SIZE / 2)
        }
        //determine whether the click was left or right of screen
        if (mouseX > VIRTUAL_WINDOW_WIDTH / 2f) {
            //right of screen, move to the right-most spot of any non-occupied board if possible
            val indx = boardManager.getRightMostCardOnBoard() + 1
            if (indx >= MAX_BOARD_SIZE) {
                println("Cannot place card,right board is full")
                return null
            }
            return indx
        }
        val indx = boardManager.getLeftMostCardOnBoard() - 1
        if (indx < 0) {
            println("Cannot place card,left board is full")
            return null
        }
        return indx
    }

    private fun getBoardPosition(boardIndex: Int): Position {
        return Position(INITIAL_BOARD_X + (CARD_WIDTH * boardIndex), INITIAL_BOARD_Y)
    }

    fun moveCardToBoard(card: DrawableCard, boardIndex: Int) {
        //we don't want to keep the drag listener from hand to board
        card.getActor().clearListeners()
        val pos = getBoardPosition(boardIndex)
        card.getActor().setPosition(pos.xPosition, pos.yPosition)
    }

    fun addCardToHand(card: DrawableCard) {
        val cardImg = card.getActor()
        cardImg.setScale(0.75f)
        val lsnr = if (card.getCardType() == CardType.TARGET_SPELL) {
            NonDraggableCardListener(card, initTargetSpellFnc = ::initTargetSpell)
        } else {
            DraggableCardListener(card, playCardFnc = ::playedCard)
        }
        updateCardPositionInHand()
        cardImg.addListener(lsnr)
        stage.addActor(cardImg)
    }

    private fun playMonsterCard(card: DrawableCard, position: Position, matchId: Int): CardPlayedOut? {
        var cardPlayedOut: CardPlayedOut? = null
        val indx = getClosestEmptyBoardIndex(position.xPosition, position.yPosition)
        if (indx != null) {
            player.boardManager.updatePlayerBoard(card, indx)
            val pos = getBoardPosition(indx)
            card.getActor().setPosition(pos.xPosition, pos.yPosition)
            cardPlayedOut = CardPlayedOut(card, indx, matchId)
        }
        return cardPlayedOut
    }

    private fun playTargetSpell(card: DrawableCard, position: Position, matchId: Int) {
        //the target; don't want to trigger on non card actor.
        val actor = stage.hit(position.xPosition, position.yPosition, true)
        if (actor is CardActor) {
            when (card) {
                is HeroPower -> {
                    val heroPowerOut = HeroPowerOut(matchId, actor.secondaryId)
                    queue.addMessage(heroPowerOut)
                    //allow hero power to trigger listener again
                    player.heroPower.getActor().touchable = Touchable.enabled

                }
                is ClientCard -> {
                    val cardPlayed = CardPlayedOut(card = card, cardTarget = actor.secondaryId, matchId = matchId)
                    queue.addMessage(cardPlayed)
                }
            }
            clearTargetingAction()
        }
    }

    private fun playNonTargetSpell(card: DrawableCard, matchId: Int): CardPlayedOut {
        return CardPlayedOut(card = card, matchId = matchId)
    }

    private fun initTargetSpell(card: DrawableCard, position: Position) {
        initialClickX = position.xPosition
        initialClickY = position.yPosition
        sourceTargetCard = card
        println("initialClickX:$initialClickY ,initialClickY:$initialClickY")
    }

    private fun playedCard(card: DrawableCard, position: Position) {
        var cardPlayedOut: Message? = null
        val cardType = card.getCardType()
        val matchId = player.getCurrentMatchId() ?: return
        println("Playing Card of type:$cardType")
        when (cardType) {
            CardType.MONSTER -> {
                cardPlayedOut = playMonsterCard(card, position, matchId)
            }
            CardType.SPELL -> {
                cardPlayedOut = playNonTargetSpell(card, matchId)
            }
            else -> {
                println("Trying to play card of invalid type:$cardType")
            }
        }
        if (cardPlayedOut == null) {
            println("Error cannot create message for cardType of $cardType")
            return
        }

        queue.addMessage(cardPlayedOut)
    }

    fun removeCardfromHand(card: DrawableCard) {
        card.getActor().remove()
        updateCardPositionInHand()
    }

    private fun updateCardPositionInHand() {
        var initialX = INITIAL_HAND_X
        for (card in player.getCardsInHand()) {
            val myCard = card as DrawableCard
            myCard.getActor().setPosition(initialX, INITIAL_HAND_Y)
            initialX += CARD_WIDTH
        }
    }

    fun render(batch: SpriteBatch, font: BitmapFont, shaper: ShapeRenderer) {
        batch.begin()

        font.draw(batch, "PLAY", 200f, 510f)
        font.draw(batch, "CONNECT", 385f, 535f)
        font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", 50f, 750f)
        font.draw(batch, "Player: ${player.getPlayerName()}", 50f, 725f)
        font.draw(batch, "HP: ${player.getCurrentPlayerLife()}/${player.getPlayerMaxLife()}", 50f, 700f)
        font.draw(batch, "Mana: ${player.getPlayerMana()}/${player.getPlayerMaxMana()}", 50f, 675f)
        font.draw(batch, "MouseX:$mouseX", 50f, 650f)
        font.draw(batch, "MouseY:$mouseY", 50f, 625f)
        batch.draw(healthHeroImg, 625f, 50f)
        font.draw(batch, player.getCurrentPlayerLife().toString(), 637.5f, 80f)
        batch.end()

        renderDebugBoard(shaper)
        if (sourceTargetCard != null) {
            val angle = 180.0 / Math.PI * Math.atan2(initialClickX.toDouble() - mouseX, mouseY.toDouble() - initialClickY)
            arrowImg.rotation = angle.toFloat()
            batch.begin()
            arrowImg.draw(batch)
            val actor = stage.hit(mouseX, mouseY, true)
            if (actor != null) {
                batch.draw(targetCircle, mouseX, mouseY)
                //only highlight targeted card
                if (actor is CardActor) {
                    batch.draw(cardTargeted, actor.x, actor.y)
                }
            }
            batch.end()
        }
    }

    private fun renderDebugBoard(shaper: ShapeRenderer) {
        //Board border for all position
        var initialboardX = INITIAL_BOARD_X
        shaper.begin(ShapeRenderer.ShapeType.Line)
        shaper.color = (Color.RED)
        for (x in 0..6) {
            shaper.rect(initialboardX, 250f, CARD_WIDTH, CARD_HEIGHT)
            initialboardX += CARD_WIDTH
        }
        shaper.end()
    }

}