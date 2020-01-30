package com.game.asura.screen

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
import com.game.asura.*
import com.game.asura.messagein.MatchStartIn
import com.game.asura.processor.MessageDispatcher
import com.game.asura.processor.MessageInProcessor
import com.game.asura.processor.MessageOutProcessor
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.graphics.use
import java.util.stream.Collectors

class MatchScreen(private val parentScreen: KtxGame<Screen>,
                  private val messageQueue: MessageQueue,
                  private val server: ServerBosom,
                  matchStart: MatchStartIn) : KtxScreen {

    private val batch: SpriteBatch = SpriteBatch()
    private val shaper: ShapeRenderer = ShapeRenderer()
    private val camera: OrthographicCamera = OrthographicCamera()
    private val viewport: FitViewport = FitViewport(VIRTUAL_WINDOW_WIDTH.toFloat(), VIRTUAL_WINDOW_HEIGHT.toFloat(), camera)
    private val stage: Stage = Stage(viewport)
    private val cardStore = CardStore()
    private val assetStore = AssetStore()
    private lateinit var font: BitmapFont

    private val player: ClientPlayer = ClientPlayer(matchStart.accountName)
    private val otherPlayer: ClientPlayer = ClientPlayer(matchStart.enemyName)
    private lateinit var messageDispatcher: MessageDispatcher
    private val uiManager: UIManager = UIManager(stage, messageQueue, player, otherPlayer, assetStore)

    init {
        setupStage()
        //setupGraphicOptions()
        setupDisplayMode()
        setupFont()
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
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
        val messageInProcessor = MessageInProcessor(player, uiManager, cardStore, parentScreen, otherPlayer, assetStore)
        val messageOutProcessor = MessageOutProcessor(server::sendMessage)
        messageDispatcher = MessageDispatcher(messageInProcessor, messageOutProcessor)
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
            messageDispatcher.onMessage(message)
        }

        camera.update()

        shaper.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined

        shaper.color = (Color.BLUE)
        shaper.use(ShapeRenderer.ShapeType.Line) {
            //Visible GameView
            shaper.rect(1f, 1f, stage.width - 1f, stage.height - 2f)
        }

        //foreground
        uiManager.render(batch, font, shaper)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        println("Resizing:Height=$height width=$width graphicW:${Gdx.graphics.width} graphicH:${Gdx.graphics.height}")
        val scaled = viewport.scaling.apply(VIRTUAL_WINDOW_WIDTH.toFloat(), VIRTUAL_WINDOW_HEIGHT.toFloat(), width.toFloat(), height.toFloat())
        val viewportWidth = Math.round(scaled.x)
        val viewportHeight = Math.round(scaled.y)

        val cropX = (width - viewportWidth) / 2f
        val cropY = (height - viewportHeight) / 2f
        val screenWidth = Gdx.graphics.width - cropX.toInt()
        val screenHeight = Gdx.graphics.height - cropY.toInt()
        println("CropX:$cropX cropY:$cropY scaleX:${scaled.x} scaledY:${scaled.y}, viewportWidth:$viewportWidth viewportHeight:$viewportHeight")
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