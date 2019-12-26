package com.game.asura

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


}