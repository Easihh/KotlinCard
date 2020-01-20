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
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.asura.*
import com.game.asura.messagein.MatchStartIn
import com.game.asura.processor.*
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use

class LoginScreen(private val parentScreen: KtxGame<Screen>,
                  private val messageQueue: MessageQueue,
                  private val server: ServerBosom) : KtxScreen {

    private val camera: OrthographicCamera = OrthographicCamera()
    private val viewport: FitViewport = FitViewport(VIRTUAL_WINDOW_WIDTH.toFloat(), VIRTUAL_WINDOW_HEIGHT.toFloat(), camera)
    private val stage: Stage = Stage(viewport)
    private val batch = SpriteBatch()
    private val outProcessor = MessageOutProcessor(server::sendMessage)
    private val inProcessor = LoginMessageInProcessor(::toPreMatchScreen)
    private val messageDispatcher = MessageDispatcher(inProcessor, outProcessor)
    private lateinit var font: BitmapFont
    private val shaper: ShapeRenderer = ShapeRenderer()
    private var canProcessMessage: Boolean = true

    init {
        setupStage()
        setupFont()
        setupConnectButton()
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
                return true
            }
        }
        connectBtn.addListener(listener)
        stage.addActor(connectBtn)
    }

    private fun toPreMatchScreen() {
        canProcessMessage = false
        parentScreen.setScreen<PreMatchScreen>()
    }

    override fun render(delta: Float) {

        while (canProcessMessage && messageQueue.queueIsNotEmpty()) {
            val message = messageQueue.nextMessage()
            messageDispatcher.onMessage(message)
        }

        batch.use {
            font.draw(it, "CONNECT", 870f, 720f)
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