package com.game.asura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.game.asura.card.CardType
import com.game.asura.messageout.AttackOut
import com.game.asura.messageout.CardPlayedOut
import com.game.asura.messageout.EndTurnOut
import com.game.asura.messaging.Message

class UIManager(private val stage: Stage,
                private val queue: InsertableQueue,
                private val player: ClientPlayer,
                private val otherPlayer: ClientPlayer) {


    private val assetStore = AssetStore()
    private var mouseX: Float = 0f
    private var mouseY: Float = 0f
    private var initialClickX: Float = 0f
    private var initialClickY: Float = 0f
    private val emptyCardBoard = Sprite(assetStore.getTexture(Asset.EMPTY_BOARD_CARD))
    private val arrowImg = Sprite(assetStore.getTexture(Asset.ARROW_POINTER))
    private val systemCursor = Cursor.SystemCursor.Hand
    private val invisibleCursor = Pixmap(Gdx.files.internal("core/assets/invisibleCursor.png"))
    private val cursor = Gdx.graphics.newCursor(invisibleCursor, 0, 0)
    private var selectedCard: DrawableCard? = null
    //to keep track of whether board card should tilt 1 index left or right due to pending card placement.
    //private var boardTilt = BoardManager.BoardPositionTilt.NONE
    private var endTurnTime: Long = System.currentTimeMillis()
    private val backgroundG = Group()
    private val foregroundG = Group()

    init {
        stage.addActor(backgroundG)
        stage.addActor(foregroundG)
        cursor.dispose()
        addNextPhaseBtn()
        setupEmptyBoardActor()
        val mouseMovedLstr = object : InputListener() {
            override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
                if (event == null) {
                    return false
                }
                mouseX = event.stageX
                mouseY = event.stageY

                selectedCard?.let {
                    if (player.handManager.cardIsInHand(it.getSecondayId())) {
                        if (it.getCardType() != CardType.TARGET_SPELL) {
                            it.actor.setPosition(mouseX - (BOARD_CARD_WIDTH / 2), mouseY - (BOARD_CARD_HEIGHT / 2))
                        }
                        if (it.getCardType() == CardType.MONSTER && !player.boardManager.boardIsEmpty()) {
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

    private fun updateBoardPosition() {
        var initialX = INITIAL_BOARD_X
        for (i in 0 until MAX_BOARD_SIZE) {
            val card = player.boardManager.getCardByBoardIndex(i)
            if (card.getCardType() != CardType.INVALID) {
                card.actor.setPosition(initialX, INITIAL_BOARD_Y)
            }
            initialX += BOARD_CARD_WIDTH
        }
    }

    private fun addNextPhaseBtn() {
        val endTurnButton = Texture(Asset.MENU_BUTTON_SMALL.path)
        val endTurn = Image(endTurnButton)
        endTurn.setPosition(VIRTUAL_WINDOW_WIDTH - 200f, 150f)
        val listener = object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val msg: Message = when (player.currentPhase) {
                    Phase.POST_ATTACK -> {
                        EndTurnOut()
                    }
                    Phase.ATTACK -> {
                        AttackOut()
                    }
                    Phase.MAIN -> {
                        EndTurnOut()
                    }
                }
                queue.addMessage(msg)

                return true
            }
        }
        endTurn.addListener(listener)
        backgroundG.addActor(endTurn)
    }

    private fun setupClickListener() {
        stage.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
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
            it.actor.touchable = Touchable.enabled
        }
        selectedCard = null
        //reset to normal cursor here
        Gdx.graphics.setSystemCursor(systemCursor)
        updateBoardPosition()

    }

    private fun getClosestBoardIndex(mouseX: Float, mouseY: Float): Int? {
        val boardManager = player.boardManager
        var initialX = INITIAL_BOARD_X
        var closestIndx: Int? = null
        for (i in 0 until MAX_BOARD_SIZE) {
            if (mouseX >= initialX && mouseX <= initialX + BOARD_CARD_WIDTH) {
                closestIndx = i
                break
            }
            initialX += BOARD_CARD_WIDTH
        }
        if (closestIndx == null) {
            return null
        }
        if (boardManager.getCardByBoardIndex(closestIndx).getCardType() != CardType.INVALID) {
            //card already exist at the wanted location!
            return null
        }

        return closestIndx
    }

    private fun getBoardPosition(boardIndex: Int): Position {
        return Position(INITIAL_BOARD_X + (BOARD_CARD_WIDTH * boardIndex), INITIAL_BOARD_Y)
    }

    fun moveCardToBoard(card: DrawableCard, boardIndex: Int) {

        //need change texture of actor as its now on board
        val cardTexture = assetStore.getCardTexture(card.getPrimaryId()) ?: return
        card.transformActor(cardTexture.onBoardTexture)
        //add new actor to stage as it was destroyed
        stage.addActor(card.actor)
        val pos = getBoardPosition(boardIndex)
        card.actor.setPosition(pos.xPosition, pos.yPosition)

        val targetListener = object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (button == Input.Buttons.LEFT) {
                    println("board card is touched.SourceTarget:$selectedCard")
                    //card is being target by something ie target spell
                    selectedCard?.let {
                        if (player.handManager.cardIsInHand(it.getSecondayId())) {
                            playedCard(it, Position(event.stageX, event.stageY))
                        }
                    }
                }

                return true
            }

        }
        card.actor.addListener(targetListener)
        updateCardPositionInHand()
    }

    fun initCardTexture(card: DrawableCard) {
        val cardTexture = assetStore.getCardTexture(card.getPrimaryId()) ?: return
        card.initCardTexture(cardTexture.inHandTexture)
    }

    fun addCardToHand(card: DrawableCard) {
        initCardTexture(card)
        val cardImg = card.actor
        cardImg.setScale(1.0f)
        val lsnr = if (card.getCardType() == CardType.TARGET_SPELL) {
            TargetableCardListener(card, initTargetSpellFnc = ::initTargetSpell)
        } else {
            DraggableCardListener(card, initSelectCardFnc = ::initCardSelect, hasCardSelectFnc = ::hasCardSelected)
        }
        updateCardPositionInHand()
        cardImg.addListener(lsnr)
        foregroundG.addActor(cardImg)
    }

    private fun playMonsterCard(card: DrawableCard, position: Position): CardPlayedOut? {
        var cardPlayedOut: CardPlayedOut? = null
        val indx = getClosestBoardIndex(position.xPosition, position.yPosition)
        if (indx != null) {
            player.boardManager.updatePlayerBoard(card, indx)
            val pos = getBoardPosition(indx)
            card.actor.setPosition(pos.xPosition, pos.yPosition)
            cardPlayedOut = CardPlayedOut(card, indx)
        }
        return cardPlayedOut
    }

    private fun playTargetSpell(card: DrawableCard, position: Position): Message? {
        var cardPlayedOut: Message? = null
        //the target; don't want to trigger on non card actor.
        val actor = stage.hit(position.xPosition, position.yPosition, true)
        if (actor is BoardCard) {
            when (card) {
                is SpellCard -> {
                    cardPlayedOut = CardPlayedOut(card = card, cardTarget = actor.secondaryId)
                }
            }
            clearTargetingAction()
        }
        return cardPlayedOut
    }

    private fun playNonTargetSpell(card: DrawableCard): CardPlayedOut {
        return CardPlayedOut(card = card)
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
        if (position.yPosition < 200) {
            println("Trying to trigger card play at position:${position.xPosition},${position.yPosition} " +
                    "which is near playerHand, assuming player want to put back card in hand.")
            clearTargetingAction()
            updateCardPositionInHand()
            return
        }
        when (cardType) {
            CardType.MONSTER -> {
                cardPlayedOut = playMonsterCard(card, position)
            }
            CardType.SPELL -> {
                cardPlayedOut = playNonTargetSpell(card)
            }
            CardType.TARGET_SPELL -> {
                cardPlayedOut = playTargetSpell(card, position)
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
        card.actor.clearListeners()
        selectedCard = null
        queue.addMessage(cardPlayedOut)
    }

    fun removeCardfromHand(card: DrawableCard) {
        card.actor.remove()
        updateCardPositionInHand()
    }

    private fun updateCardPositionInHand() {
        var initialX = INITIAL_HAND_X
        for (card in player.handManager.getCardsInHand()) {
            val myCard = card as DrawableCard
            myCard.actor.setPosition(initialX, INITIAL_HAND_Y)
            initialX += BOARD_CARD_WIDTH
        }
    }

    private fun getPhaseStartBtnText(): String {
        if (player.currentPhase == Phase.ATTACK) {
            return "ATTACK"
        }
        return "END TURN"
    }

    fun render(batch: SpriteBatch, font: BitmapFont, shaper: ShapeRenderer) {
        batch.begin()
        if (player.myTurn) {
            font.draw(batch, getPhaseStartBtnText(), 835f, 195f)
        } else {
            font.draw(batch, "Enemy Turn", 835f, 195f)
        }
        font.draw(batch, "FPS: ${Gdx.graphics.framesPerSecond}", 50f, 950f)
        font.draw(batch, "Player: ${player.playerName}", 50f, 200f)
        font.draw(batch, "Player: ${otherPlayer.playerName}", 50f, 925f)
        font.draw(batch, "Mana: ${player.currentMana}/${player.maxMana}", 50f, 175f)
        font.draw(batch, "EnemyMana: ${otherPlayer.currentMana}/${otherPlayer.maxMana}", 50f, 850f)
        font.draw(batch, "Mouse:$mouseX,$mouseY", 50f, 825f)
        font.draw(batch, "Time:${(endTurnTime - System.nanoTime()) / ONE_NANO_SECOND}", 50f, 800f)
        batch.draw(assetStore.getTexture(Asset.HEALTH_ICON_BIG), 575f, 100f)
        batch.draw(assetStore.getTexture(Asset.HEALTH_ICON_BIG), 575f, 800f)
        font.draw(batch, player.playerLifePoint.toString(), 587.5f, 80f)
        font.draw(batch, otherPlayer.playerLifePoint.toString(), 587.5f, 825f)
        batch.end()

        renderDebugBoard(batch, shaper)
        selectedCard?.let {
            if (it.getCardType() == CardType.TARGET_SPELL) {
                val angle = 180.0 / Math.PI * Math.atan2(initialClickX.toDouble() - mouseX, mouseY.toDouble() - initialClickY)
                arrowImg.rotation = angle.toFloat()
                batch.begin()
                arrowImg.draw(batch)
                val actor = stage.hit(mouseX, mouseY, true)
                if (actor != null) {
                    if (actor is BoardCard) {
                        batch.draw(assetStore.getTexture(Asset.TARGET_CIRCLE), mouseX, mouseY)
                        //only highlight targeted card
                        batch.draw(assetStore.getTexture(Asset.CARD_TARGETED), actor.x, actor.y)
                    }
                }
                batch.end()
            }
            if (it.getCardType() == CardType.MONSTER && player.boardManager.cardIsPresentOnBoard(it.getSecondayId())) {
                val angle = 180.0 / Math.PI * Math.atan2(initialClickX.toDouble() - mouseX, mouseY.toDouble() - initialClickY)
                arrowImg.rotation = angle.toFloat()
                batch.begin()
                arrowImg.draw(batch)
                val actor = stage.hit(mouseX, mouseY, true)
                if (actor != null) {
                    if (actor is BoardCard) {
                        batch.draw(assetStore.getTexture(Asset.TARGET_CIRCLE), mouseX, mouseY)
                        //only highlight targeted card
                        batch.draw(assetStore.getTexture(Asset.CARD_TARGETED), actor.x, actor.y)
                    }
                }
                batch.end()
            }
            batch.begin()
            batch.end()
        }
        renderBoardCardStats(batch, font)
    }

    private fun renderDebugBoard(batch: SpriteBatch, shaper: ShapeRenderer) {
        batch.begin()
        //draw enemy board
        var initialboardX = INITIAL_BOARD_X
        for (x in 0 until MAX_BOARD_SIZE) {
            if (otherPlayer.boardManager.getCardByBoardIndex(x).getCardType() == CardType.INVALID) {
                batch.draw(emptyCardBoard, initialboardX, 500f)
            }
            initialboardX += BOARD_CARD_WIDTH
        }
        batch.end()
    }

    private fun setupEmptyBoardActor() {
        //draw our own board
        var initialboardX = INITIAL_BOARD_X
        for (x in 0 until MAX_BOARD_SIZE) {
            val card = player.boardManager.getCardByBoardIndex(x)
            if (card.getCardType() == CardType.INVALID) {
                card.initCardTexture(emptyCardBoard.texture)
                card.actor.setPosition(initialboardX, 300f)
                backgroundG.addActor(card.actor)
            }
            initialboardX += BOARD_CARD_WIDTH
        }
    }

    private fun renderBoardCardStats(batch: SpriteBatch, font: BitmapFont) {
        batch.begin()
        for (x in 0 until MAX_BOARD_SIZE) {
            val card = player.boardManager.getCardByBoardIndex(x)
            if (card.getCardType() != CardType.INVALID && card is MinionCard) {
                //draw attack
                batch.draw(assetStore.getTexture(Asset.ATTACK_ICON_SMALL), card.actor.x + 6f, card.actor.y + 6f)
                font.draw(batch, card.getAttack().toString(), card.actor.x + 12f, card.actor.y + 24f)

                //draw health
                batch.draw(assetStore.getTexture(Asset.HEALTH_ICON_SMALL), card.actor.x + BOARD_CARD_WIDTH - 32, card.actor.y + 6f)
                font.draw(batch, card.getHealth().toString(), card.actor.x + BOARD_CARD_WIDTH - 24f, card.actor.y + 24f)
            }
        }
        batch.end()
    }

    fun startTurnTimer() {
        endTurnTime = System.nanoTime() + (SECOND_PER_TURN * ONE_NANO_SECOND)
    }
}