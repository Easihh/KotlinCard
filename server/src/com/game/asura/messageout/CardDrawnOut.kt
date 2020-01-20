package com.game.asura.messageout

import com.game.asura.ServerMinionCard
import com.game.asura.account.ChannelMessageWriter
import com.game.asura.card.Card
import com.game.asura.card.CardType
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

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

        if (card.getCardType() == CardType.MONSTER) {
            val monster = card as ServerMinionCard
            val attack = monster.getAttack()
            val health = monster.getHealth()
            val maxHealth = monster.getMaxHealth()
            messageBuilder.add(MessageField.CARD_ATTACK, attack)
            messageBuilder.add(MessageField.CARD_HEALTH, health)
            messageBuilder.add(MessageField.CARD_MAX_HEALTH, maxHealth)
        }
    }
}