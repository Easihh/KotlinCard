package com.game.asura.messageout

import com.game.asura.DrawableCard
import com.game.asura.END_MESSAGE_DELIMITER
import com.game.asura.messaging.MessageBuilder
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

class MonsterAttackOut(private val matchId: Int,
                       private val card: DrawableCard,
                       private val target: Int) : OutMessage {
    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.MONSTER_ATTACK.value)
        messageBuilder.add(MessageField.PRIMARY_CARD_ID, card.getPrimaryId())
        messageBuilder.add(MessageField.SECONDARY_CARD_ID, card.getSecondayId())
        messageBuilder.add(MessageField.MATCH_ID, matchId)
        messageBuilder.add(MessageField.CARD_TARGET, target)
        messageBuilder.add(MessageField.END_MESSAGE, END_MESSAGE_DELIMITER)

        messageBuilder.flip()
    }
}