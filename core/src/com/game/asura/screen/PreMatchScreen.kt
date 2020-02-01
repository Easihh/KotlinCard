package com.game.asura.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.asura.*
import com.game.asura.messagein.MatchStartIn
import com.game.asura.messageout.PlayGameRequestOut
import com.game.asura.processor.MessageDispatcher
import com.game.asura.processor.MessageOutProcessor
import com.game.asura.processor.PreMatchMessageInProcessor
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use

class PreMatchScreen(private val parentScreen: KtxGame<Screen>,
                     private val messageQueue: MessageQueue,
                     private val server: ServerBosom) : KtxScreen {

    private val camera: OrthographicCamera = OrthographicCamera()
    private val viewport: FitViewport = FitViewport(VIRTUAL_WINDOW_WIDTH.toFloat(), VIRTUAL_WINDOW_HEIGHT.toFloat(), camera)
    private val stage: Stage = Stage(viewport)
    private val batch = SpriteBatch()
    private val outProcessor = MessageOutProcessor(server::sendMessage)
    private val inProcessor = PreMatchMessageInProcessor(::toMatchScreen)
    private val messageDispatcher = MessageDispatcher(inProcessor, outProcessor)
    private lateinit var font: BitmapFont
    private val shaper: ShapeRenderer = ShapeRenderer()
    private var canProcessMessage: Boolean = true
    private val skin = Skin(Gdx.files.internal("core/assets/uiskin.json"))
    private val findingMatchDlg = Dialog("New Game..", skin)

    init {
        setupStage()
        setupFont()
        setupPlayButton()
        setupCollection()
        setupOption()
    }

    private fun setupStage() {
        println("Current DisplayMode:${Gdx.graphics.displayMode}")
        stage.addListener(object : InputListener() {
            override fun keyDown(event: InputEvent, keycode: Int): Boolean {
                println("Exiting the application.")
                if (keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit()
                    return false
                }
                return false
            }
        })
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
        canProcessMessage = true
    }

    private fun setupFont() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/8-BIT.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 28
        font = generator.generateFont(parameter)
        generator.dispose()
    }


    private fun setupPlayButton() {
        val playButton = Texture(Asset.MENU_BUTTON.path)
        val playBtn = Image(playButton)
        playBtn.setPosition(400f, 450f)
        playBtn.setScale(1.0f, 1.0f)
        val listener = object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                println("Requesting PlayGame.")
                val playRequest = PlayGameRequestOut()
                messageQueue.addMessage(playRequest)
                createWaitingForGameScreen()
                return false
            }
        }
        playBtn.addListener(listener)
        stage.addActor(playBtn)
    }

    private fun createWaitingForGameScreen() {
        findingMatchDlg.setPosition(Gdx.graphics.width / 4f, Gdx.graphics.height / 2f)
        findingMatchDlg.setSize(VIRTUAL_WINDOW_WIDTH/2f, 256f)
        val lbl = Label.LabelStyle(font, Color.WHITE)
        findingMatchDlg.text("WAITING FOR PLAYER...\n", lbl)

        findingMatchDlg.isMovable = false
        findingMatchDlg.isModal = true
        stage.addActor(findingMatchDlg)
    }


    private fun setupCollection() {
        val collectButton = Texture(Asset.MENU_BUTTON.path)
        val playBtn = Image(collectButton)
        playBtn.setPosition(400f, 375f)
        playBtn.setScale(1.0f, 1.0f)
        stage.addActor(playBtn)
    }

    private fun setupOption() {

    }

    private fun toMatchScreen(matchStartIn: MatchStartIn) {
        canProcessMessage = false
        findingMatchDlg.remove()
        parentScreen.addScreen(MatchScreen(parentScreen, messageQueue, server, matchStartIn))
        parentScreen.setScreen<MatchScreen>()
    }

    override fun render(delta: Float) {

        while (canProcessMessage && messageQueue.queueIsNotEmpty()) {
            val message = messageQueue.nextMessage()
            messageDispatcher.onMessage(message)
        }

        shaper.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        batch.use {
            font.draw(it, "Battle", 500f, 487.5f)
            font.draw(it, "Collection", 475f, 412.5f)
        }

        //view
        shaper.color = Color.SCARLET
        shaper.use(ShapeRenderer.ShapeType.Line) {
            shaper.rect(1f, 1f, stage.width - 1f, stage.height - 2f)
        }
        stage.act()
        stage.draw()

    }

    override fun resize(width: Int, height: Int) {
       // println("Resizing:Height=$height width=$width graphicW:${Gdx.graphics.width} graphicH:${Gdx.graphics.height}")
        val scaled = viewport.scaling.apply(VIRTUAL_WINDOW_WIDTH.toFloat(), VIRTUAL_WINDOW_HEIGHT.toFloat(), width.toFloat(), height.toFloat())
        val viewportWidth = Math.round(scaled.x)
        val viewportHeight = Math.round(scaled.y)

        val cropX = (width - viewportWidth) / 2f
        val cropY = (height - viewportHeight) / 2f
        val screenWidth = Gdx.graphics.width - cropX.toInt()
        val screenHeight = Gdx.graphics.height - cropY.toInt()
       // println("CropX:$cropX cropY:$cropY scaleX:${scaled.x} scaledY:${scaled.y}, viewportWidth:$viewportWidth viewportHeight:$viewportHeight")

        viewport.setScreenBounds((cropX / 2).toInt(), cropY.toInt(),
                screenWidth, screenHeight)
        viewport.apply(true)
    }

    override fun dispose() {
        // Will be automatically disposed of by the game instance.
        font.dispose()
        batch.dispose()
    }
}