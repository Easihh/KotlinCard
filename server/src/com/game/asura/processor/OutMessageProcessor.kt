package com.game.asura.processor

import com.game.asura.messageout.ServerOutMessage

class OutMessageProcessor {

    fun onMessage(message: ServerOutMessage) {
        message.send()
    }
}