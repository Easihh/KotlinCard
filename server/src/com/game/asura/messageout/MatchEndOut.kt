package com.game.asura.messageout

import com.game.asura.MatchResult
import com.game.asura.account.ChannelMessageWriter
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class MatchEndOut(channelWriter: ChannelMessageWriter,
                  private val accountName: String,
                  private val matchResult: MatchResult) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.MATCH_END.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accountName)
        messageBuilder.add(MessageField.MATCH_RESULT, matchResult.value)
    }
}