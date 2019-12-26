package com.game.asura.processor

import com.game.asura.DecodedMessage
import com.game.asura.Message
import com.game.asura.messageout.OutMessage

class MessageProcessor(private val inProcessor: MessageInProcessor,
                       private val outProcessor: MessageOutProcessor) {

    fun onMessage(msg: Message) {
        when (msg) {
            is DecodedMessage -> {
                inProcessor.onMessage(msg)
            }
            is OutMessage -> {
                outProcessor.onMessage(msg)
            }
        }
    }
}