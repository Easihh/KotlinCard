package com.game.asura.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.asura.*
import com.game.asura.processor.LoginMessageInProcessor
import com.game.asura.processor.MessageDispatcher
import com.game.asura.processor.MessageOutProcessor
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
        /*stage.addListener(object : InputListener() {
            /** Called when a key goes down. When true is returned, the event is [handled][Event.handle].  */
            override fun keyDown(event: InputEvent, keycode: Int): Boolean {
                println("Exiting the application.")
                if (keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit()
                    return false
                }
                return false
            }
        })*/
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
        stage.addActor(connectBtn)

        val uiskin = Skin(Gdx.files.internal("core/assets/uiskin.json"))
        val accountNameStr = Label("Account Name", uiskin)
        accountNameStr.color = Color.WHITE
        accountNameStr.height = 64f
        accountNameStr.width = 256f
        accountNameStr.setPosition(400f, 575f)
        stage.addActor(accountNameStr)
        val accountName = TextField("DefaultTextHere", uiskin)
        accountName.color = Color.DARK_GRAY
        accountName.height = 64f
        accountName.width = 256f
        accountName.setPosition(400f, 500f)
        stage.addActor(accountName)

        val accountPasswordStr = Label("Account Password", uiskin)
        accountPasswordStr.color = Color.WHITE
        accountPasswordStr.height = 64f
        accountPasswordStr.width = 256f
        accountPasswordStr.setPosition(400f, 425f)
        stage.addActor(accountPasswordStr)
        val accountPassw = TextField("DefaultPassword", uiskin)
        accountPassw.isPasswordMode = true
        accountPassw.setPasswordCharacter('*')
        accountPassw.color = Color.DARK_GRAY
        accountPassw.height = 64f
        accountPassw.width = 256f
        accountPassw.setPosition(400f, 350f)
        stage.addActor(accountPassw)

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
                if (!server.connect(accountName.text)) {
                    connFailed.show(stage)
                }

                return true
            }
        }
        connectBtn.addListener(listener)
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

        shaper.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        batch.use {
            font.draw(it, "CONNECT", 870f, 720f)
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