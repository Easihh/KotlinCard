package com.game.asura.messageout

import com.game.asura.account.ChannelMessageWriter
import com.game.asura.card.Card
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class SpellCardPlayedOut(channelWriter: ChannelMessageWriter,
                         private val accountName: String,
                         private val card: Card,
                         private val target: Int?) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.CARD_PLAYED.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accountName)
        messageBuilder.add(MessageField.PRIMARY_CARD_ID, card.getPrimaryId())
        messageBuilder.add(MessageField.SECONDARY_CARD_ID, card.getSecondayId())
        messageBuilder.add(MessageField.CARD_COST, card.getCost())
        messageBuilder.add(MessageField.CARD_TYPE, card.getCardType().value)
        if (target != null) {
            messageBuilder.add(MessageField.CARD_TARGET, target)
        }

    }
}