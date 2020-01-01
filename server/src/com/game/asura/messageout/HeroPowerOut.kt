package com.game.asura.messageout

import com.game.asura.*
import com.game.asura.account.ChannelMessageWriter

class HeroPowerOut(channelWriter: ChannelMessageWriter,
                   private val accountName: String,
                   private val target: Int?) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.HERO_POWER.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accountName)
        if (target != null) {
            messageBuilder.add(MessageField.CARD_TARGET, target)
        }
    }
}