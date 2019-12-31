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
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.game.asura.messageout.CardPlayedOut

class UIManager(private val stage: Stage,
                private val playerAccount: PlayerAccount,
                private val queue: InsertableQueue) {

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


    init {
        cursor.dispose()
        setupHeroPower()
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

    private fun setupClickListener() {
        val heroPower = playerAccount.player.heroPower.getImage()
        stage.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (event == null){
                    return false
                }
                if (button == Input.Buttons.LEFT) {
                    if (playerAccount.player.heroPower.isActive()) {
                        val actor = stage.hit(event.stageX, event.stageY, true)
                        if (actor is CardActor) {
                            println("Do action on :${actor.secondaryId}")
                        }
                        //check if valid target before doing action
                        return false
                    }
                }
                if (button == Input.Buttons.RIGHT) {
                    if (playerAccount.player.heroPower.isActive()) {
                        playerAccount.player.heroPower.deactivate()
                        //reset to normal cursor here
                        Gdx.graphics.setSystemCursor(systemCursor)
                        //allow hero power to trigger listener again
                        heroPower.touchable = Touchable.enabled
                        return false
                    }
                }
                return false
            }
        })
    }


    private fun setupHeroPower() {
        val heroPower = playerAccount.player.heroPower.getImage()
        heroPower.setPosition(400f, 50f)
        val intputlstr = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (event == null) {
                    return false
                }
                Gdx.graphics.setCursor(cursor)

                initialClickX = event.stageX
                initialClickY = event.stageY
                playerAccount.player.heroPower.activate()

                println("initialHeroPower clickX:$initialClickX,clickY:$initialClickY")

                //should only apply to targetable hero power..

                //so that another click on hero power won't be triggered by stage hit until target action is canceled
                heroPower.touchable = Touchable.disabled
                return false
            }
        }
        heroPower.addListener(intputlstr)
        stage.addActor(heroPower)
    }

    fun getClosestEmptyBoardIndex(mouseX: Float, mouseY: Float): Int? {
        val boardManager = playerAccount.player.boardManager
        val player = playerAccount.player
        if (boardManager.boardIsEmpty()) {
            return (MAX_BOARD_SIZE / 2)
        }
        //determine whether the click was left or right of screen
        if (mouseX > WINDOW_WIDTH / 2) {
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
        cardImg.setScale(1.0f)
        val dragLisnr = object : DragListener() {
            override fun drag(event: InputEvent, x: Float, y: Float, pointer: Int) {
                if (card.getCardType() != CardType.TARGET_SPELL) {
                    cardImg.moveBy(x - cardImg.width / 2, y - cardImg.height / 2)
                }
            }

            override fun dragStop(event: InputEvent, x: Float, y: Float, pointer: Int) {
                cardImg.removeListener(this)
                var cardPlayedOut: CardPlayedOut? = null
                val cardType = card.getCardType()
                println("Playing Card of type:$cardType")
                if (cardType == CardType.MONSTER) {
                    //rethink use of bord position here;useless
                    val indx = getClosestEmptyBoardIndex(event.stageX, event.stageY)
                    if (indx != null) {
                        playerAccount.player.boardManager.updatePlayerBoard(card, indx)
                        val pos = getBoardPosition(indx)
                        cardImg.setPosition(pos.xPosition, pos.yPosition)
                        val currentMatch = playerAccount.getCurrentMatch() ?: return
                        cardPlayedOut = CardPlayedOut(card, indx, currentMatch.matchId)
                    }
                }
                if (cardType == CardType.SPELL) {
                    val currentMatch = playerAccount.getCurrentMatch() ?: return
                    cardPlayedOut = CardPlayedOut(card = card, matchId = currentMatch.matchId)
                }
                if (cardType == CardType.TARGET_SPELL) {
                    //should find what im targeting
                    //val target=getTarget()
                    val currentMatch = playerAccount.getCurrentMatch() ?: return
                    cardPlayedOut = CardPlayedOut(card = card, matchId = currentMatch.matchId, cardTarget = 7)
                }
                if (cardPlayedOut == null) {
                    println("Error cannot create message for cardType of $cardType")
                    return
                }
                queue.addMessage(cardPlayedOut)
            }
        }
        updateCardPositionInHand()
        cardImg.addListener(dragLisnr)
        stage.addActor(cardImg)
    }

    fun removeCardfromHand(card: DrawableCard) {
        card.getActor().remove()
        updateCardPositionInHand()
    }

    private fun updateCardPositionInHand() {
        var initialX = INITIAL_HAND_X
        for (card in playerAccount.player.getCardsInHand()) {
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
        font.draw(batch, "Player: ${playerAccount.player.getPlayerName()}", 50f, 725f)
        font.draw(batch, "HP: ${playerAccount.player.getCurrentPlayerLife()}/${playerAccount.player.getPlayerMaxLife()}", 50f, 700f)
        font.draw(batch, "Mana: ${playerAccount.player.getPlayerMana()}/${playerAccount.player.getPlayerMaxMana()}", 50f, 675f)
        font.draw(batch, "MouseX:$mouseX", 50f, 650f)
        font.draw(batch, "MouseY:$mouseY", 50f, 625f)
        batch.end()

        renderDebugBoard(shaper)
        if (playerAccount.player.heroPower.isActive()) {
            val angle = 180.0 / Math.PI * Math.atan2(initialClickX.toDouble() - mouseX, mouseY.toDouble() - initialClickY)
            arrowImg.rotation = angle.toFloat()
            batch.begin()
            arrowImg.draw(batch)
            val actor = stage.hit(mouseX, mouseY, true)
            if (actor != null) {
                batch.draw(targetCircle, mouseX, mouseY)
                batch.draw(cardTargeted, actor.x, actor.y)
            }

            batch.end()
            /*shaper.begin(ShapeRenderer.ShapeType.Line)
            shaper.color = Color.RED
            shaper.line(initialClickX, initialClickY, mouseX, mouseY)
            shaper.end()*/
        }
    }

    private fun renderDebugBoard(shaper: ShapeRenderer) {
        //Board border for all position
        var initialboardX = INITIAL_BOARD_X
        shaper.begin(ShapeRenderer.ShapeType.Line)
        shaper.color = (Color.RED)
        for (x in 0..6) {
            shaper.rect(initialboardX, 250f, 128f, 192f)
            initialboardX += 128f
        }
        shaper.end()
    }

}