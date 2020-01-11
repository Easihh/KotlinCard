package com.game.asura.messageout

import com.game.asura.account.ChannelMessageWriter
import com.game.asura.card.HeroCard
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class MatchInfoOut(channelWriter: ChannelMessageWriter,
                   private val accountName: String,
                   private val enemyName: String,
                   private val playerHero: HeroCard,
                   private val enemyHero: HeroCard) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.MATCH_INFO.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accountName)
        messageBuilder.add(MessageField.HERO_PRIMARY_ID, playerHero.getPrimaryId())
        messageBuilder.add(MessageField.HERO_SECONDARY_ID, playerHero.getSecondayId())
        messageBuilder.add(MessageField.ENEMY_ACCOUNT_NAME, enemyName)
        messageBuilder.add(MessageField.ENEMY_HERO_PRIMARY_ID, enemyHero.getPrimaryId())
        messageBuilder.add(MessageField.ENEMY_HERO_SECONDARY_ID, enemyHero.getSecondayId())

    }
}