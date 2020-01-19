package com.game.asura

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.game.asura.messagein.MatchInfoIn
import com.game.asura.processor.MessageInProcessor
import com.game.asura.processor.MessageOutProcessor
import com.game.asura.processor.MessageProcessor
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use
import java.util.stream.Collectors

class MatchScreen(private val parentScreen: KtxGame<Screen>,
                  private val messageQueue: MessageQueue,
                  private val server: ServerBosom,
                  private val matchInfo: MatchInfoIn) : KtxScreen {

    private val batch: SpriteBatch = SpriteBatch()
    private val shaper: ShapeRenderer = ShapeRenderer()
    private val camera: OrthographicCamera = OrthographicCamera()
    private val viewport: FitViewport = FitViewport(VIRTUAL_WINDOW_WIDTH.toFloat(), VIRTUAL_WINDOW_HEIGHT.toFloat(), camera)
    private val stage: Stage = Stage(viewport)
    private lateinit var font: BitmapFont

    private lateinit var player: ClientPlayer
    private lateinit var otherPlayer: ClientPlayer
    private lateinit var messageProcessor: MessageProcessor
    private lateinit var uiManager: UIManager

    init {
        setupStage()
        //setupGraphicOptions()
        setupDisplayMode()
        setupFont()
    }

    private fun setupPlayer() {
        player = ClientPlayer(matchInfo.accountName, MagePower(), matchInfo.primaryHeroId, matchInfo.primaryHeroId)
        otherPlayer = ClientPlayer(matchInfo.enemyName, MagePower(), matchInfo.enemyPrimaryHeroId, matchInfo.enemySecondaryHeroId)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
        setupPlayer()
        uiManager = UIManager(stage, messageQueue, player, otherPlayer)
        setupMessageProcessors()
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

    private fun setupFont() {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("core/assets/font/8-BIT.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 28
        font = generator.generateFont(parameter)
        generator.dispose()
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
        val graphicOptions: SelectBox<Graphics.DisplayMode> = SelectBox(skin)
        graphicOptions.setPosition(550f, 500f)
        graphicOptions.setSize(200f, 25f)
        var displayModeStr: MutableList<Graphics.DisplayMode> = ArrayList()
        Gdx.graphics.displayModes.forEach { displayModeStr.add(it) }
        displayModeStr = displayModeStr.stream().filter { it.height >= VIRTUAL_WINDOW_HEIGHT && it.width >= VIRTUAL_WINDOW_WIDTH }.collect(Collectors.toList())
        val array: Array<Graphics.DisplayMode> = Array()
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

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        while (messageQueue.queueIsNotEmpty()) {
            val message = messageQueue.nextMessage()
            messageProcessor.onMessage(message)
        }

        camera.update()

        shaper.projectionMatrix = camera.combined

        shaper.color = (Color.BLUE)
        shaper.use(ShapeRenderer.ShapeType.Line) {
            //Visible GameView
            shaper.rect(0f, 0f, stage.width, stage.height)
        }
        //foreground
        shaper.color = Color.BLACK
        shaper.use(ShapeRenderer.ShapeType.Filled) {
            shaper.rect(1f, 1f, stage.width, stage.height)
        }
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
        // Will be automatically disposed of by the game instance.
        font.dispose()
        batch.dispose()
    }
}