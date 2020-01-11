package com.game.asura.messageout

import com.game.asura.*
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class PlayGameRequestOut : OutMessage {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.GAME_REQUEST.value)
        messageBuilder.add(MessageField.END_MESSAGE, END_MESSAGE_DELIMITER)
        messageBuilder.flip()
    }
}