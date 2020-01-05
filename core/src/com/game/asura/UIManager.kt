package com.game.asura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
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


    private val assetStore = AssetStore()
    private var mouseX: Float = 0f
    private var mouseY: Float = 0f
    private var initialClickX: Float = 0f
    private var initialClickY: Float = 0f
    private val arrowImg = Sprite(assetStore.getTexture(Asset.ARROW_POINTER))
    private val systemCursor = Cursor.SystemCursor.Hand
    private val invisibleCursor = Pixmap(Gdx.files.internal("core/assets/invisibleCursor.png"))
    private val cursor = Gdx.graphics.newCursor(invisibleCursor, 0, 0)
    private var selectedCard: DrawableCard? = null
    //to keep track of whether board card should tilt 1 index left or right due to pending card placement.
    private var boardTilt = BoardManager.BoardPositionTilt.NONE


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

                selectedCard?.let {
                    if (it.getCardType() != CardType.TARGET_SPELL) {
                        it.getActor().setPosition(mouseX - (BOARD_CARD_WIDTH / 2), mouseY - (BOARD_CARD_HEIGHT / 2))
                    }
                    if (it.getCardType() == CardType.MONSTER && !player.boardManager.boardIsEmpty()) {
                        val halfScreen = VIRTUAL_WINDOW_WIDTH / 2f
                        val currentTilt = if (event.stageX >= halfScreen) {
                            BoardManager.BoardPositionTilt.LEFT
                        } else {
                            BoardManager.BoardPositionTilt.RIGHT
                        }
                        if (currentTilt != boardTilt) {
                            boardTilt = currentTilt
                            moveCardByTilt()
                            updateBoardPosition()

                        }
                    }
                }
                arrowImg.setPosition(mouseX + 16f, mouseY - 16f)
                return true
            }
        }
        stage.addListener(mouseMovedLstr)
        setupClickListener()
    }

    private fun moveCardByTilt() {
        when (boardTilt) {
            BoardManager.BoardPositionTilt.LEFT -> {
                player.boardManager.moveCardLeft()
            }
            BoardManager.BoardPositionTilt.RIGHT -> {
                player.boardManager.moveCardRight()
            }
            else -> {
                //don't need to reposition board
                return
            }
        }
    }

    private fun updateBoardPosition() {
        var initialX = INITIAL_BOARD_X
        for (i in 0 until MAX_BOARD_SIZE) {
            val card = player.boardManager.getCardByBoardIndex(i)
            card.getActor().setPosition(initialX, INITIAL_BOARD_Y)
            initialX += BOARD_CARD_WIDTH
        }
    }

    private fun addHeroToBoard() {
        val actor = player.getActor()
        actor.setPosition(500f, 50f)
        stage.addActor(actor)
    }

    private fun setupClickListener() {
        stage.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val matchId = player.getCurrentMatchId()
                if (matchId == null) {
                    println("Error, current match is null.")
                    return true
                }
                if (button == Input.Buttons.RIGHT) {
                    selectedCard?.let {
                        println("clearing target action for card:$selectedCard")
                        clearTargetingAction()
                    }
                    //move card back to hand
                    updateCardPositionInHand()
                    return true
                }
                return true
            }
        })
    }

    private fun clearTargetingAction() {
        //allow hero power/card to trigger listener again via stage.hit
        selectedCard?.let {
            it.getActor().touchable = Touchable.enabled
        }
        selectedCard = null
        //reset to normal cursor here
        Gdx.graphics.setSystemCursor(systemCursor)
        //move back card to previous value
        when (boardTilt) {
            BoardManager.BoardPositionTilt.LEFT -> {
                player.boardManager.moveCardRight()
            }
            BoardManager.BoardPositionTilt.RIGHT -> {
                player.boardManager.moveCardLeft()
            }
            else -> {
                //do nothing
            }
        }
        updateBoardPosition()
        boardTilt = BoardManager.BoardPositionTilt.NONE

    }


    private fun setupHeroPower() {
        val heroPower = player.heroPower.getActor()
        heroPower.setPosition(675f, 50f)
        val intputlstr = object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                println("Hero Power click triggered.")
                Gdx.graphics.setCursor(cursor)

                initialClickX = event.stageX
                initialClickY = event.stageY
                selectedCard = player.heroPower

                //so that another click on hero power won't be triggered by stage hit until target action is canceled
                heroPower.touchable = Touchable.disabled
                return true
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
        return Position(INITIAL_BOARD_X + (BOARD_CARD_WIDTH * boardIndex), INITIAL_BOARD_Y)
    }

    fun moveCardToBoard(card: DrawableCard, boardIndex: Int) {
        //we don't want to keep the drag listener from hand to board
        card.getActor().clearListeners()

        //need change texture of actor as its now on board
        card.transformActor(assetStore.getTexture(Asset.BOARD_CARD))
        val pos = getBoardPosition(boardIndex)
        card.getActor().setPosition(pos.xPosition, pos.yPosition)

        val targetListener = object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                println("board card is touched.SourceTarget:$selectedCard")
                if (selectedCard == null) {
                    initCardSelect(card, Position(event.stageX, event.stageY))
                    //disable actor as we don't want to trigger more mouse click on itself
                    // until our targeting is done
                    card.getActor().touchable = Touchable.disabled
                    return true
                }
                //card is being target by something ie target spell/hero power etc
                selectedCard?.let {
                    playedCard(it, Position(event.stageX, event.stageY))
                }

                return true
            }

        }
        card.getActor().addListener(targetListener)

        updateCardPositionInHand()
    }

    fun addCardToHand(card: DrawableCard) {
        val cardImg = card.getActor()
        cardImg.setScale(0.75f)
        val lsnr = if (card.getCardType() == CardType.TARGET_SPELL) {
            TargetableCardListener(card, initTargetSpellFnc = ::initTargetSpell)
        } else {
            DraggableCardListener(card, initSelectCardFnc = ::initCardSelect, hasCardSelectFnc = ::hasCardSelected)
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

    private fun playTargetSpell(card: DrawableCard, position: Position, matchId: Int): Message? {
        var cardPlayedOut: Message? = null
        //the target; don't want to trigger on non card actor.
        val actor = stage.hit(position.xPosition, position.yPosition, true)
        if (actor is CardActor) {
            when (card) {
                is HeroPower -> {
                    cardPlayedOut = HeroPowerOut(matchId, actor.secondaryId)

                }
                is ClientCard -> {
                    cardPlayedOut = CardPlayedOut(card = card, cardTarget = actor.secondaryId, matchId = matchId)
                }
            }
            clearTargetingAction()
        }
        return cardPlayedOut
    }

    private fun playNonTargetSpell(card: DrawableCard, matchId: Int): CardPlayedOut {
        return CardPlayedOut(card = card, matchId = matchId)
    }

    private fun hasCardSelected(): Boolean {
        return selectedCard != null
    }

    private fun initTargetSpell(card: DrawableCard, position: Position) {
        if (!hasCardSelected()) {
            initialClickX = position.xPosition
            initialClickY = position.yPosition
            selectedCard = card
            Gdx.graphics.setCursor(cursor)
            return
        }
        //we already have selectedCard try play card with a target at our clicked pos
        playedCard(card, position)
    }

    private fun initCardSelect(card: DrawableCard, position: Position) {
        if (!hasCardSelected()) {
            selectedCard = card
            return
        }
        //we already have selectedCard try play card
        playedCard(card, position)
    }

    private fun playedCard(card: DrawableCard, position: Position) {
        var cardPlayedOut: Message? = null
        val cardType = card.getCardType()
        val matchId = player.getCurrentMatchId() ?: return
        if (position.yPosition < 200) {
            println("Trying to trigger card play at position:${position.xPosition},${position.yPosition} " +
                    "which is near playerHand, assuming player want to put back card in hand.")
            clearTargetingAction()
            updateCardPositionInHand()
            return
        }
        when (cardType) {
            CardType.MONSTER -> {
                cardPlayedOut = playMonsterCard(card, position, matchId)
            }
            CardType.SPELL -> {
                cardPlayedOut = playNonTargetSpell(card, matchId)
            }
            CardType.TARGET_SPELL -> {
                cardPlayedOut = playTargetSpell(card, position, matchId)
            }
            else -> {
                println("Trying to play card of invalid type:$cardType")
            }
        }
        if (cardPlayedOut == null) {
            //target of action/click is invalid
            return
        }
        println("Playing Card of type:$cardType")

        //remove card input listener as it was played
        card.getActor().clearListeners()
        selectedCard = null
        queue.addMessage(cardPlayedOut)
    }

    fun removeCardfromHand(card: DrawableCard) {
        card.getActor().remove()
        updateCardPositionInHand()
    }

    private fun updateCardPositionInHand() {
        var initialX = INITIAL_HAND_X
        for (card in player.handManager.getCardsInHand()) {
            val myCard = card as DrawableCard
            myCard.getActor().setPosition(initialX, INITIAL_HAND_Y)
            initialX += BOARD_CARD_WIDTH
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
        batch.draw(assetStore.getTexture(Asset.HEALTH_ICON_BIG), 625f, 50f)
        font.draw(batch, player.getCurrentPlayerLife().toString(), 637.5f, 80f)
        batch.end()

        renderDebugBoard(shaper)
        selectedCard?.let {
            if (it.getCardType() == CardType.TARGET_SPELL) {
                val angle = 180.0 / Math.PI * Math.atan2(initialClickX.toDouble() - mouseX, mouseY.toDouble() - initialClickY)
                arrowImg.rotation = angle.toFloat()
                batch.begin()
                arrowImg.draw(batch)
                val actor = stage.hit(mouseX, mouseY, true)
                if (actor != null) {
                    if (actor is CardActor && actor.targetable()) {
                        batch.draw(assetStore.getTexture(Asset.TARGET_CIRCLE), mouseX, mouseY)
                        //only highlight targeted card
                        batch.draw(assetStore.getTexture(Asset.CARD_TARGETED), actor.x, actor.y)
                    }
                }
                batch.end()
            }
            batch.begin()
            //highlight selected card, but not if using hero power
            if (it !is HeroPower) {
                batch.draw(assetStore.getTexture(Asset.CARD_SELECTED), it.getActor().x, it.getActor().y)
            }
            batch.end()
        }
        renderBoardCardStats(batch, font)
    }

    private fun renderDebugBoard(shaper: ShapeRenderer) {
        //Board border for all position
        var initialboardX = INITIAL_BOARD_X
        shaper.begin(ShapeRenderer.ShapeType.Line)
        shaper.color = (Color.RED)
        for (x in 0..6) {
            shaper.rect(initialboardX, 250f, BOARD_CARD_WIDTH, BOARD_CARD_HEIGHT)
            initialboardX += BOARD_CARD_WIDTH
        }
        shaper.end()
    }

    private fun renderBoardCardStats(batch: SpriteBatch, font: BitmapFont) {
        batch.begin()
        for (x in 0..6) {
            val card = player.boardManager.getCardByBoardIndex(x)
            if (card.getCardType() != CardType.INVALID) {
                if (card.getAttack() != null) {
                    batch.draw(assetStore.getTexture(Asset.ATTACK_ICON_SMALL), card.getActor().x + 6f, card.getActor().y + 6f)
                    font.draw(batch, card.getAttack().toString(), card.getActor().x + 12f, card.getActor().y + 24f)
                }
                if (card.getHealth() != null) {
                    batch.draw(assetStore.getTexture(Asset.HEALTH_ICON_SMALL), card.getActor().x + BOARD_CARD_WIDTH - 32, card.getActor().y + 6f)
                    font.draw(batch, card.getHealth().toString(), card.getActor().x + BOARD_CARD_WIDTH - 24f, card.getActor().y + 24f)
                }
            }
        }
        batch.end()
    }
}