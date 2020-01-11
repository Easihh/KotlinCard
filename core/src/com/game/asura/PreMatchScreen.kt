package com.game.asura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.asura.messagein.MatchInfoIn
import com.game.asura.messageout.PlayGameRequestOut
import com.game.asura.processor.PreMatchMessageInProcessor
import com.game.asura.processor.PreMatchMessageOutProcessor
import com.game.asura.processor.PreMatchMessageProcessor
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
    private val outProcessor = PreMatchMessageOutProcessor(server::sendMessage)
    private val inProcessor = PreMatchMessageInProcessor(::toMatchScreen)
    private val preMatchProcessor = PreMatchMessageProcessor(inProcessor, outProcessor)
    private lateinit var font: BitmapFont
    private var canProcessMessage: Boolean = true

    init {
        setupStage()
        setupFont()
        setupConnectButton()
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
    }

    private fun setupFont() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/8-BIT.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 28
        font = generator.generateFont(parameter)
        generator.dispose()
    }


    private fun setupPlayButton() {
        val playButton = Texture(Asset.MENU_BUTTON_SMALL.path)
        val playBtn = Image(playButton)
        playBtn.setPosition(715f, 675f)
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

    private fun setupConnectButton() {
        val img = Texture(Asset.MENU_BUTTON_SMALL.path)
        val connectBtn = Image(img)
        connectBtn.setPosition(850f, 675f)
        val listener = object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                val skin = Skin(Gdx.files.internal("core/assets/uiskin.json"))
                val connFailed = Dialog("Error", skin)
                connFailed.setPosition(Gdx.graphics.displayMode.width / 2f, Gdx.graphics.displayMode.height / 2f)
                val lbl = Label.LabelStyle(font, Color.WHITE)
                connFailed.text("CONNECTION TO SERVER FAILED\n SERVER MAY BE DOWN", lbl)
                val okBtn = TextButton("OK", skin)
                val lsnr = object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        Gdx.app.exit()
                    }
                }
                connFailed.buttonTable.defaults().height(64f)
                connFailed.buttonTable.defaults().width(96f)
                okBtn.addListener(lsnr)
                connFailed.button(okBtn)
                //connFailed.setSize(VIRTUAL_WINDOW_WIDTH.to, 256f)
                connFailed.isMovable = false
                connFailed.isModal = true
                if (!server.connect()) {
                    connFailed.show(stage)
                }
                return false
            }
        }
        connectBtn.addListener(listener)
        stage.addActor(connectBtn)
    }

    private fun toMatchScreen(matchInfoIn: MatchInfoIn) {
        canProcessMessage = false
        parentScreen.addScreen(MatchScreen(parentScreen, messageQueue, server, matchInfoIn))
        parentScreen.setScreen<MatchScreen>()
    }

    override fun render(delta: Float) {

        while (canProcessMessage && messageQueue.queueIsNotEmpty()) {
            val message = messageQueue.nextMessage()
            preMatchProcessor.onMessage(message)
        }

        batch.use {
            font.draw(it, "PLAY", 745f, 720f)
            font.draw(it, "CONNECT", 870f, 720f)
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