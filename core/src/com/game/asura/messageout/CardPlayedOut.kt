package com.game.asura.messageout

import com.game.asura.*
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class CardPlayedOut(private val card: DrawableCard,
                    private val boardIndex: Int? = null,
                    private val matchId: Int,
                    private val cardTarget: Int? = null) : OutMessage {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.CARD_PLAYED.value)
        messageBuilder.add(MessageField.PRIMARY_CARD_ID, card.getPrimaryId())
        messageBuilder.add(MessageField.SECONDARY_CARD_ID, card.getSecondayId())
        messageBuilder.add(MessageField.CARD_TYPE, card.getCardType().value)
        messageBuilder.add(MessageField.MATCH_ID, matchId)
        if (cardTarget != null) {
            messageBuilder.add(MessageField.CARD_TARGET, cardTarget)
        }
        if (boardIndex != null) {
            messageBuilder.add(MessageField.BOARD_POSITION, boardIndex)
        }
        messageBuilder.add(MessageField.END_MESSAGE, END_MESSAGE_DELIMITER)

        messageBuilder.flip()
    }
}