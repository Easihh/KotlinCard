package com.game.asura.messageout

import com.game.asura.LoginStatus
import com.game.asura.account.ChannelMessageWriter
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class LoginRequestReplyOut(channelWriter: ChannelMessageWriter,
                           private val replyState: LoginStatus) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.LOGIN_REPLY.value)
        messageBuilder.add(MessageField.LOGIN_STATUS, replyState.value)
    }
}