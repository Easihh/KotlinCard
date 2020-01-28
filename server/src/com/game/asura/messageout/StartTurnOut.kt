package com.game.asura.messageout

import com.game.asura.Phase
import com.game.asura.account.ChannelMessageWriter
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class StartTurnOut(channelWriter: ChannelMessageWriter,
                   private val phase: Phase) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.START_TURN.value)
        messageBuilder.add(MessageField.NEXT_PHASE, phase.value)
    }
}