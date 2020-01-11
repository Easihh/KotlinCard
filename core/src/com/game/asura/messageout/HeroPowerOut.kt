package com.game.asura.messageout

import com.game.asura.END_MESSAGE_DELIMITER
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class HeroPowerOut(private val target: Int) : OutMessage {
    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.HERO_POWER.value)
        messageBuilder.add(MessageField.CARD_TARGET, target)
        messageBuilder.add(MessageField.END_MESSAGE, END_MESSAGE_DELIMITER)

        messageBuilder.flip()
    }
}