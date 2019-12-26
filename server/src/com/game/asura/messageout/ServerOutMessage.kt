package com.game.asura.messageout

import com.game.asura.account.ChannelMessageWriter
import com.game.asura.Message
import com.game.asura.MessageBuilder

abstract class ServerOutMessage(private val channelWriter: ChannelMessageWriter) : Message {
    protected abstract fun build(messageBuilder: MessageBuilder)

    fun send() {
        build(channelWriter.messageBuilder)
        channelWriter.sendMessage()
    }
}