package com.game.asura.messageout

import com.game.asura.*
import com.game.asura.account.ChannelMessageWriter
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class ConnStatusOut(channelWriter: ChannelMessageWriter) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.CONNECTION_STATE.value)
        messageBuilder.add(MessageField.CONN_STATUS, ConnectionStatus.CONNECTED.value)
    }
}