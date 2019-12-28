package com.game.asura.messageout

import com.game.asura.*
import com.game.asura.account.ChannelMessageWriter

class CardPlayedOut(channelWriter: ChannelMessageWriter,
                    private val accountName: String,
                    private val card: Card,
                    private val target: Int?,
                    private val boardPosition: Int?) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.CARD_PLAYED.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accountName)
        messageBuilder.add(MessageField.PRIMARY_CARD_ID, card.getPrimaryId())
        messageBuilder.add(MessageField.SECONDARY_CARD_ID, card.getSecondayId())
        messageBuilder.add(MessageField.CARD_COST, card.getCost())
        messageBuilder.add(MessageField.CARD_TYPE, card.getCardType().value)
        if (boardPosition != null) {
            messageBuilder.add(MessageField.BOARD_POSITION, boardPosition)
        }
        if (target != null) {
            messageBuilder.add(MessageField.CARD_TARGET, target)
        }
    }
}