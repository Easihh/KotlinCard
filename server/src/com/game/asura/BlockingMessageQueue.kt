package com.game.asura

import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque

class BlockingMessageQueue : InsertableQueue {

    private val messageQueue: BlockingDeque<Message> = LinkedBlockingDeque()

    fun take(): Message {
        return messageQueue.take()
    }

    override fun addMessage(msg: Message) {
        messageQueue.add(msg)
    }
}