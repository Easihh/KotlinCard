package com.game.asura.messageout

import com.game.asura.END_MESSAGE_DELIMITER
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType


class LoginRequestOut(private val accountName: String) : OutMessage {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.LOGIN_REQUEST.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accountName)
        messageBuilder.add(MessageField.END_MESSAGE, END_MESSAGE_DELIMITER)
        messageBuilder.flip()
    }
}