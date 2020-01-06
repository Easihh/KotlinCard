package com.game.asura

import com.game.asura.messaging.Message
import java.util.concurrent.ConcurrentLinkedDeque

class MessageQueue : InsertableQueue {

    private val messageQueue: ConcurrentLinkedDeque<Message> = ConcurrentLinkedDeque()

    fun nextMessage(): Message {
        return messageQueue.poll()
    }


    fun queueIsNotEmpty(): Boolean {
        return messageQueue.isNotEmpty()
    }

    override fun addMessage(msg: Message) {
        messageQueue.add(msg)
    }

    override fun addMessage(msg: Message, delayMs: Long) {

    }

}