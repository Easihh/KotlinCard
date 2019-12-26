package com.game.asura.processor

import com.game.asura.*
import com.game.asura.messageout.ServerOutMessage

class MessageProcessor(private val inProcessor: InMessageProcessor,
                       private val outProcessor: OutMessageProcessor) {

    fun onMessage(message: Message) {

        when (message) {
            is DecodedMessage -> {
                inProcessor.onMessage(message)
            }
            is ServerOutMessage -> {
                outProcessor.onMessage(message)
            }
        }
    }
}