package com.game.asura.messageout

import com.game.asura.account.ChannelMessageWriter
import com.game.asura.card.Minion
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class MonsterDeathOut(channelWriter: ChannelMessageWriter,
                      private val card: Minion) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.MONSTER_DEATH.value)
        messageBuilder.add(MessageField.PRIMARY_CARD_ID, card.getPrimaryId())
        messageBuilder.add(MessageField.SECONDARY_CARD_ID, card.getSecondayId())
    }
}