package com.game.asura.messageout

import com.game.asura.END_MESSAGE_DELIMITER
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class EndTurnOut(private val matchId: Int) : OutMessage {
    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.END_TURN.value)
        messageBuilder.add(MessageField.MATCH_ID, matchId)
        messageBuilder.add(MessageField.END_MESSAGE, END_MESSAGE_DELIMITER)

        messageBuilder.flip()
    }
}