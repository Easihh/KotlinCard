package com.game.asura

import com.game.asura.messaging.Message

interface InsertableQueue {

    fun addMessage(msg: Message)

    fun addMessage(msg: Message, delayMs: Long)
}