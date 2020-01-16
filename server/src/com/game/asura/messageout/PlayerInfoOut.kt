package com.game.asura.messageout

import com.game.asura.account.ChannelMessageWriter
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class PlayerInfoOut(channelWriter: ChannelMessageWriter,
                    private val accoutName: String,
                    private val currentMana: Int,
                    private val maxMana: Int) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.PLAYER_INFO.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accoutName)
        messageBuilder.add(MessageField.PLAYER_CURRENT_MANA, currentMana)
        messageBuilder.add(MessageField.PLAYER_MAX_MANA, maxMana)
    }
}