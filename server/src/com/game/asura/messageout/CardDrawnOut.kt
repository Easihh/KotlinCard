package com.game.asura.messageout

import com.game.asura.*
import com.game.asura.account.ChannelMessageWriter

class CardDrawnOut(channelWriter: ChannelMessageWriter,
                   private val card: Card,
                   private val deckSize: Int) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.CARD_DRAWN.value)
        messageBuilder.add(MessageField.PRIMARY_CARD_ID, card.getPrimaryId())
        messageBuilder.add(MessageField.SECONDARY_CARD_ID, card.getSecondayId())
        messageBuilder.add(MessageField.CARD_COST, card.getCost())
        messageBuilder.add(MessageField.CARD_TYPE, card.getCardType().value)
        messageBuilder.add(MessageField.DECK_SIZE, deckSize)
    }
}