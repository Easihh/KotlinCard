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
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
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

    init {
        setupStage()
        setupFont()
        setupPlayButton()
    }

    private fun setupStage() {
        println("Current DisplayMode:${Gdx.graphics.displayMode}")
        stage.addListener(object : InputListener() {
            /** Called when a key goes down. When true is returned, the event is [handled][Event.handle].  */
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
                return false
            }
        }
        playBtn.addListener(listener)
        stage.addActor(playBtn)
    }

    private fun toMatchScreen(matchStartIn: MatchStartIn) {
        canProcessMessage = false
        parentScreen.addScreen(MatchScreen(parentScreen, messageQueue, server, matchStartIn))
        parentScreen.setScreen<MatchScreen>()
    }

    override fun render(delta: Float) {

        while (canProcessMessage && messageQueue.queueIsNotEmpty()) {
            val message = messageQueue.nextMessage()
            messageDispatcher.onMessage(message)
        }

        batch.use {
            font.draw(it, "PLAY", 500f, 487.5f)
        }

        //view
        shaper.color = Color.SCARLET
        shaper.use(ShapeRenderer.ShapeType.Line) {
            shaper.rect(1f, 1f, stage.width - 1f, stage.height - 1f)
        }
        stage.act()
        stage.draw()

    }

    override fun dispose() {
        // Will be automatically disposed of by the game instance.
        font.dispose()
        batch.dispose()
    }
}