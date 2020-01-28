package com.game.asura.messageout

import com.game.asura.ServerMinionCard
import com.game.asura.account.ChannelMessageWriter
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class MonsterCardPlayedOut(channelWriter: ChannelMessageWriter,
                           private val accountName: String,
                           private val card: ServerMinionCard,
                           private val boardPosition: Int) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.MONSTER_CARD_PLAYED.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accountName)
        messageBuilder.add(MessageField.PRIMARY_CARD_ID, card.getPrimaryId())
        messageBuilder.add(MessageField.SECONDARY_CARD_ID, card.getSecondayId())
        messageBuilder.add(MessageField.CARD_COST, card.getCost())
        messageBuilder.add(MessageField.CARD_TYPE, card.getCardType().value)
        messageBuilder.add(MessageField.BOARD_POSITION, boardPosition)
        messageBuilder.add(MessageField.CARD_ATTACK, card.getAttack())
        messageBuilder.add(MessageField.CARD_HEALTH, card.getHealth())
        messageBuilder.add(MessageField.CARD_MAX_HEALTH, card.getMaxHealth())
    }
}