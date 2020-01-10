package com.game.asura

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.Graphics.DisplayMode
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.game.asura.card.AllCard
import com.game.asura.messageout.PlayGameRequestOut
import com.game.asura.processor.MessageInProcessor
import com.game.asura.processor.MessageOutProcessor
import com.game.asura.processor.MessageProcessor
import java.util.stream.Collectors


class MyGdxGame : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var shaper: ShapeRenderer
    private lateinit var stage: Stage
    private lateinit var camera: OrthographicCamera
    private lateinit var viewport: FitViewport
    private lateinit var font: BitmapFont

    private val messageQueue = MessageQueue()
    private val server = ServerBosom(messageQueue)
    private lateinit var player: ClientPlayer
    private lateinit var messageProcessor: MessageProcessor
    private lateinit var uiManager: UIManager

    override fun create() {

        batch = SpriteBatch()
        shaper = ShapeRenderer()
        setupStage()
        //setupGraphicOptions()
        setupDisplayMode()
        setupFont()
        player = ClientPlayer("test", MagePower(),AllCard.MAGE_HERO.id,99999)
        val otherPlayer = ClientPlayer("enemy", MagePower(),AllCard.MAGE_HERO.id,7777)
        uiManager = UIManager(stage, messageQueue, player, otherPlayer)
        setupMessageProcessors()
        setupConnectButton()
        setupPlayButton()

        Gdx.input.inputProcessor = stage
    }

    private fun setupStage() {
        camera = OrthographicCamera()
        viewport = FitViewport(VIRTUAL_WINDOW_WIDTH.toFloat(), VIRTUAL_WINDOW_HEIGHT.toFloat(), camera)
        stage = Stage(viewport)
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

    private fun setupMessageProcessors() {
        val messageInProcessor = MessageInProcessor(player, uiManager, CardStore())
        val messageOutProcessor = MessageOutProcessor(server::sendMessage)
        messageProcessor = MessageProcessor(messageInProcessor, messageOutProcessor)
    }

    private fun setupDisplayMode() {
        if (Gdx.graphics.isFullscreen) {
            println("Setting FullScreenMode to:${Gdx.graphics.displayMode}")
            Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
        } else {
            Gdx.graphics.setWindowedMode(VIRTUAL_WINDOW_WIDTH, VIRTUAL_WINDOW_HEIGHT)
        }
    }

    private fun setupGraphicOptions() {
        val skin = Skin(Gdx.files.internal("core/assets/uiskin.json"))
        val graphicOptions: SelectBox<DisplayMode> = SelectBox(skin)
        graphicOptions.setPosition(550f, 500f)
        graphicOptions.setSize(200f, 25f)
        var displayModeStr: MutableList<DisplayMode> = ArrayList()
        Gdx.graphics.displayModes.forEach { displayModeStr.add(it) }
        displayModeStr = displayModeStr.stream().filter { it.height >= VIRTUAL_WINDOW_HEIGHT && it.width >= VIRTUAL_WINDOW_WIDTH }.collect(Collectors.toList())
        val array: Array<DisplayMode> = Array()
        displayModeStr.forEach { array.add(it) }
        graphicOptions.items = array
        val changeListener = object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                val displayMode = graphicOptions.selected
                resize(displayMode.width, displayMode.height)
            }

        }
        graphicOptions.addListener(changeListener)
        stage.addActor(graphicOptions)

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

    private fun setupFont() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/8-BIT.ttf"))
        val parameter = FreeTypeFontParameter()
        parameter.size = 28
        font = generator.generateFont(parameter)
        generator.dispose()
    }

    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        while (messageQueue.queueIsNotEmpty()) {
            val message = messageQueue.nextMessage()
            messageProcessor.onMessage(message)
        }

        camera.update()

        shaper.projectionMatrix = camera.combined

        //background
        shaper.begin(ShapeRenderer.ShapeType.Line)
        shaper.color = (Color.BLUE)
        //Visible GameView
        shaper.rect(0f, 0f, stage.width, stage.height)
        shaper.end()

        stage.act()
        stage.draw()

        //foreground
        uiManager.render(batch, font, shaper)

    }

    override fun resize(width: Int, height: Int) {
        println("Resizing:Height=$height width=$width graphicW:${Gdx.graphics.width} graphicH:${Gdx.graphics.height}")
        val scaled = viewport.scaling.apply(VIRTUAL_WINDOW_WIDTH.toFloat(), VIRTUAL_WINDOW_HEIGHT.toFloat(), width.toFloat(), height.toFloat())
        val viewportWidth = Math.round(scaled.x)
        val viewportHeight = Math.round(scaled.y)

        val cropX = (width - viewportWidth) / 2f
        val cropY = (height - viewportHeight) / 2f
        println("CropX:$cropX cropY:$cropY")
        viewport.setScreenBounds(cropX.toInt(), cropY.toInt(),
                Gdx.graphics.width - cropX.toInt(), Gdx.graphics.height)
        viewport.apply(true)
    }

    override fun dispose() {
        batch.dispose()
    }

}
