package com.game.asura

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
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
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.game.asura.messageout.PlayGameRequestOut
import com.game.asura.processor.MessageInProcessor
import com.game.asura.processor.MessageOutProcessor
import com.game.asura.processor.MessageProcessor


class MyGdxGame : ApplicationAdapter() {
    private lateinit var batch: SpriteBatch
    private lateinit var shaper: ShapeRenderer
    private lateinit var stage: Stage
    private lateinit var camera: OrthographicCamera
    private lateinit var font: BitmapFont

    private val messageQueue = MessageQueue()
    private val server = ServerBosom(messageQueue)

    private lateinit var playerAccount: PlayerAccount
    private lateinit var messageProcessor: MessageProcessor
    private lateinit var uiManager: UIManager

    override fun create() {

        batch = SpriteBatch()
        shaper = ShapeRenderer()
        setupStage()
        setupFont()
        setupPlayer()
        uiManager = UIManager(stage, playerAccount, messageQueue)
        setupMessageProcessors()
        setupConnectButton()
        setupPlayButton()

        Gdx.input.inputProcessor = stage
    }

    private fun setupStage() {
        camera = OrthographicCamera(WINDOW_WIDTH, WINDOW_HEIGHT)
        val viewport = ScreenViewport(camera)
        stage = Stage(viewport)
        stage.addListener(object : InputListener() {
            /** Called when a key goes down. When true is returned, the event is [handled][Event.handle].  */
            override fun keyDown(event: InputEvent, keycode: Int): Boolean {
                println("Exiting the appliation.")
                if (keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit()
                }
                return false
            }
        })
    }

    private fun setupPlayer() {
        val player = ClientPlayer("test", MagePower())
        playerAccount = PlayerAccount(player)
    }

    private fun setupMessageProcessors() {
        val messageInProcessor = MessageInProcessor(playerAccount, uiManager)
        val messageOutProcessor = MessageOutProcessor(server::sendMessage)
        messageProcessor = MessageProcessor(messageInProcessor, messageOutProcessor)
    }

    private fun setupPlayButton() {
        val playButton = Texture("core/assets/playBtn.png")
        val playBtn = Image(playButton)
        playBtn.setPosition(100f, 450f)
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
        val img = Texture("core/assets/playBtn.png")
        val connectBtn = Image(img)
        connectBtn.setPosition(325f, 475f)
        val listener = object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                printDisplayInfo()
                //val atlas = TextureAtlas("core/assets/uiskin.atlas")
                val skin = Skin(Gdx.files.internal("core/assets/uiskin.json"))
                //skin.addRegions( TextureAtlas(Gdx.files.internal("core/assets/uiskin.atlas")))
                //skin.add("default-font", font)
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
                connFailed.setSize(WINDOW_WIDTH, 256f)
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


    private fun printDisplayInfo() {
        println("Width:" + Gdx.app.graphics.width)
        println("Height:" + Gdx.app.graphics.height)
        val currentDisplay = Gdx.graphics.displayMode
        println("current DisplayMode:$currentDisplay")
    }


    override fun render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        while (messageQueue.queueIsNotEmpty()) {
            val message = messageQueue.nextMessage()
            messageProcessor.onMessage(message)
        }
        camera.update()
        //batch.projectionMatrix = camera.combined
        shaper.projectionMatrix = camera.combined

        //Board border for all position
        var initialboardX = INITIAL_BOARD_X
        shaper.begin(ShapeRenderer.ShapeType.Line)
        shaper.color = (Color.RED)
        for (x in 0..6) {
            shaper.rect(initialboardX, 250f, 128f, 192f)
            initialboardX += 128f
        }
        shaper.end()
        //draw actual board
        initialboardX = INITIAL_BOARD_X
        batch.begin()
        for (card in playerAccount.player.boardManager.getPlayerBoard()) {
            if (card.getCardType() != CardType.INVALID) {
                batch.draw(card.getTexture(), initialboardX, 250f, 128f, 192f)
            }
            initialboardX += 128f
        }
        batch.end()
        uiManager.render(batch, font, shaper)

        shaper.begin(ShapeRenderer.ShapeType.Line)

        shaper.color = (Color.RED)
        //Visible GameView
        shaper.rect(0f, 0f, stage.width, stage.height)

        shaper.end()

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        println("Resizing:Height=$height width=$width")

        //camera = OrthographicCamera(width.toFloat(), height.toFloat())
        //camera.setToOrtho(false)
        //stage.viewport.camera = camera
        //stage.viewport.setWorldSize(width.toFloat(), height.toFloat())

        //so that thing are drawn at center of drawable area (not within black border)
        val xDif = width - WINDOW_WIDTH.toInt()
        //camera.translate(-1 * xDif.toFloat() / 2, 0f)
    }

    override fun dispose() {
        batch.dispose()
        //img.dispose()
    }

}
