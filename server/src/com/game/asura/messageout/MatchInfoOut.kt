package com.game.asura.messageout

import com.game.asura.account.ChannelMessageWriter
import com.game.asura.card.HeroCard
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class MatchInfoOut(channelWriter: ChannelMessageWriter,
                   private val accountName: String,
                   private val playerHero: HeroCard,
                   private val matchId: Int) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.MATCH_INFO.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accountName)
        messageBuilder.add(MessageField.PRIMARY_CARD_ID, playerHero.getPrimaryId())
        messageBuilder.add(MessageField.SECONDARY_CARD_ID, playerHero.getSecondayId())
        messageBuilder.add(MessageField.MATCH_ID, matchId)
    }
}