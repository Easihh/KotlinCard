package com.game.asura.messageout

import com.game.asura.*
import com.game.asura.account.ChannelMessageWriter

class StartTurnOut(channelWriter: ChannelMessageWriter,
                   private val matchId: Int) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.START_TURN.value)
        messageBuilder.add(MessageField.MATCH_ID, matchId)
    }
}