package com.game.asura

import com.badlogic.gdx.Screen
import com.game.asura.screen.LoginScreen
import com.game.asura.screen.PreMatchScreen
import ktx.app.KtxGame


class MyGdxGame : KtxGame<Screen>() {

    private val messageQueue = MessageQueue()
    private val server = ServerBosom(messageQueue)

    override fun create() {
        addScreen(LoginScreen(this, messageQueue, server))
        addScreen(PreMatchScreen(this, messageQueue, server))
        setScreen<LoginScreen>()
    }
}
