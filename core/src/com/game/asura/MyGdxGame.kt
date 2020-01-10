package com.game.asura

import com.badlogic.gdx.Screen
import ktx.app.KtxGame


class MyGdxGame : KtxGame<Screen>() {

    private val messageQueue = MessageQueue()
    private val server = ServerBosom(messageQueue)

    override fun create() {
        addScreen(PreMatchScreen(this, messageQueue, server))
        addScreen(MatchScreen(this, messageQueue, server))
        setScreen<PreMatchScreen>()
    }
}
