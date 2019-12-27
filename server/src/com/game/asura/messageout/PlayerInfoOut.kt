package com.game.asura.messageout

import com.game.asura.*
import com.game.asura.account.ChannelMessageWriter

class PlayerInfoOut(channelWriter: ChannelMessageWriter,
                    private val accoutName: String,
                    private val changedFields: List<ChangedField>) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.PLAYER_INFO.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accoutName)

        for (field in changedFields) {
            messageBuilder.add(field.field, field.value)
        }
    }
}