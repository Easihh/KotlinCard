package com.game.asura

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.game.asura.messageout.CardPlayedOut

class UIManager(private val stage: Stage,
                private val playerAccount: PlayerAccount,
                private val queue: InsertableQueue) {

    private var mouseX: Float = 0f
    private var mouseY: Float = 0f
    private var initialClickX: Float = 0f
    private var initialClickY: Float = 0f
    private val targetCircle = Texture("core/assets/target.png")


    init {
        setupHeroPower()
        val mouseMovedLstr = object : InputListener() {
            override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
                if (event == null) {
                    return false
                }
                mouseX = event.stageX
                mouseY = event.stageY
                return false
            }
        }
        stage.addListener(mouseMovedLstr)
    }


    private fun setupHeroPower() {
        val heroPower = playerAccount.player.heroPower.getImage()
        heroPower.setPosition(400f, 50f)
        val intputlstr = object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (event == null) {
                    return false
                }
                initialClickX = event.stageX
                initialClickY = event.stageY
                playerAccount.player.heroPower.activate()
                return false
            }
        }
        heroPower.addListener(intputlstr)
        stage.addActor(heroPower)
    }

    fun getClosestEmptyBoardPosition(mouseX: Float, mouseY: Float): BoardPosition? {
        val player = playerAccount.player
        if (player.boardIsEmpty()) {
            val midPositionIndex = (MAX_BOARD_SIZE / 2)
            return getBoardPosition(midPositionIndex)
        }
        //determine whether the click was left or right of screen
        if (mouseX > WINDOW_WIDTH / 2) {
            //right of screen, move to the right-most spot of any non-occupied board if possible
            val indx = player.getRightMostCardOnBoard() + 1
            if (indx >= MAX_BOARD_SIZE) {
                println("Cannot place card,right board is full")
                return null
            }
            return getBoardPosition(indx)
        }
        val indx = player.getLeftMostCardOnBoard() - 1
        if (indx < 0) {
            println("Cannot place card,left board is full")
            return null
        }
        return getBoardPosition(indx)
    }

    private fun getBoardPosition(indx: Int): BoardPosition {
        val pos = Position(INITIAL_BOARD_X + (CARD_WIDTH * indx), INITIAL_BOARD_Y)
        return BoardPosition(pos, indx)
    }

    fun addCardToHand(card: DrawableCard) {
        val cardImg = card.getImage()
        cardImg.setScale(0.75f)
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
                if (cardType == CardType.MONSTER) {
                    val pos = getClosestEmptyBoardPosition(event.stageX, event.stageY)
                    if (pos != null) {
                        playerAccount.player.updatePlayerBoard(card, pos)
                        cardImg.setPosition(pos.position.xPosition, pos.position.yPosition)
                        val currentMatch = playerAccount.getCurrentMatch() ?: return
                        cardPlayedOut = CardPlayedOut(card, pos, currentMatch.matchId)
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
        card.getImage().remove()
        updateCardPositionInHand()
    }

    private fun updateCardPositionInHand() {
        var initialX = INITIAL_HAND_X
        for (card in playerAccount.player.getCardsInHand()) {
            val myCard = card as DrawableCard
            myCard.getImage().setPosition(initialX, INITIAL_HAND_Y)
            initialX += CARD_WIDTH
        }
    }

    fun render(batch: SpriteBatch, font: BitmapFont, shaper: ShapeRenderer) {
        batch.begin()

        font.draw(batch, "PLAY", 200f, 510f)
        font.draw(batch, "CONNECT", 385f, 535f)
        font.draw(batch, "Player: ${playerAccount.player.getPlayerName()}", 50f, 725f)
        font.draw(batch, "HP: ${playerAccount.player.getCurrentPlayerLife()}/${playerAccount.player.getPlayerMaxLife()}", 50f, 700f)
        font.draw(batch, "Mana: ${playerAccount.player.getPlayerMana()}/${playerAccount.player.getPlayerMaxMana()}", 50f, 675f)
        font.draw(batch, "MouseX:$mouseX", 50f, 650f)
        font.draw(batch, "MouseY:$mouseY", 50f, 625f)
        batch.end()

        if (playerAccount.player.heroPower.isActive()) {
            batch.begin()
            batch.draw(targetCircle, mouseX-32f, mouseY-32f)
            batch.end()
            shaper.begin(ShapeRenderer.ShapeType.Line)
            shaper.color = Color.RED
            shaper.line(initialClickX, initialClickY, mouseX, mouseY)
            shaper.end()
        }
    }


}