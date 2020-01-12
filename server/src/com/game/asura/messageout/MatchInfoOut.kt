package com.game.asura.messageout

import com.game.asura.ServerHeroCard
import com.game.asura.account.ChannelMessageWriter
import com.game.asura.card.Minion
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class MatchInfoOut(channelWriter: ChannelMessageWriter,
                   private val accountName: String,
                   private val enemyName: String,
                   private val playerMonster: ServerHeroCard,
                   private val enemyMonster: ServerHeroCard) : ServerOutMessage(channelWriter) {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.MATCH_INFO.value)
        messageBuilder.add(MessageField.ACCOUNT_NAME, accountName)
        messageBuilder.add(MessageField.HERO_PRIMARY_ID, playerMonster.getPrimaryId())
        messageBuilder.add(MessageField.HERO_SECONDARY_ID, playerMonster.getSecondayId())
        messageBuilder.add(MessageField.ENEMY_ACCOUNT_NAME, enemyName)
        messageBuilder.add(MessageField.ENEMY_HERO_PRIMARY_ID, enemyMonster.getPrimaryId())
        messageBuilder.add(MessageField.ENEMY_HERO_SECONDARY_ID, enemyMonster.getSecondayId())

    }
}