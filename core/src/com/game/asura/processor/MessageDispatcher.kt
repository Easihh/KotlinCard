package com.game.asura.processor

import com.game.asura.parsing.DecodedMessage
import com.game.asura.messaging.Message
import com.game.asura.messageout.OutMessage

class MessageDispatcher(private val inProcessor: MessageProcessor<DecodedMessage>,
                        private val outProcessor: MessageProcessor<OutMessage>) {

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