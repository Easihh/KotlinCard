package com.game.asura.messageout

import com.game.asura.*
import com.game.asura.account.ChannelMessageWriter
import com.game.asura.card.BaseCard
import com.game.asura.card.Minion
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class CardInfoOut(channelWriter: ChannelMessageWriter,
                  private val accoutName: String,
                  private val card: BaseCard,
                  private val changedFields: List<ChangedField>) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.CARD_INFO.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accoutName)
        messageBuilder.add(MessageField.PRIMARY_CARD_ID, card.getPrimaryId())
        messageBuilder.add(MessageField.SECONDARY_CARD_ID, card.getSecondayId())

        for (field in changedFields) {
            messageBuilder.add(field.field, field.value)
        }
    }
}