package com.game.asura.account

import com.game.asura.END_MESSAGE_DELIMITER
import com.game.asura.MessageBuilder
import com.game.asura.MessageField
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class ChannelMessageWriter(private val channel: SocketChannel,
                           private val writeBuffer: ByteBuffer) {
    val messageBuilder = MessageBuilder(writeBuffer)

    fun sendMessage() {
        messageBuilder.add(MessageField.END_MESSAGE, END_MESSAGE_DELIMITER)
        messageBuilder.flip()
        println("message sent:${messageBuilder.printMessage()}")
        channel.write(writeBuffer)
        messageBuilder.clear()
    }
}