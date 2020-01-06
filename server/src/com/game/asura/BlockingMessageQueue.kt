package com.game.asura

import com.game.asura.messaging.Message
import java.util.*
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import kotlin.concurrent.schedule

class BlockingMessageQueue : InsertableQueue {

    private val messageQueue: BlockingDeque<Message> = LinkedBlockingDeque()

    fun take(): Message {
        return messageQueue.take()
    }

    override fun addMessage(msg: Message) {
        messageQueue.add(msg)
    }

    override fun addMessage(msg: Message, delayMs: Long) {
        Timer("Delayed Message", false).schedule(delayMs) {
            addMessage(msg)
        }
    }
}