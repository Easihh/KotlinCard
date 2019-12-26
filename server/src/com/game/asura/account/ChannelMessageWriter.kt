package com.game.asura.account

import com.game.asura.MessageBuilder
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class ChannelMessageWriter(private val channel: SocketChannel,
                           private val writeBuffer: ByteBuffer) {
    val messageBuilder = MessageBuilder(writeBuffer)

    fun sendMessage() {
        println("message sent:${messageBuilder.printMessage()}")
        channel.write(writeBuffer)
        messageBuilder.clear()
    }
}