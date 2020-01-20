package com.game.asura.processor

import com.game.asura.messaging.Message

interface MessageProcessor<M : Message> {

    fun onMessage(msg: M)
}