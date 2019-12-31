package com.game.asura.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.game.asura.MyGdxGame
import com.game.asura.ServerBosom


fun main(arg: Array<String>) {
    val config = LwjglApplicationConfiguration()
    config.forceExit = false
    config.title = "Some Title"
    config.width = 1024
    config.height = 768
    config.resizable = true
    config.fullscreen = true
    val myGame = MyGdxGame()
    LwjglApplication(myGame, config)
}

