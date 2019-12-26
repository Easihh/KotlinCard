package com.game.asura.messageout

import com.game.asura.GameType
import com.game.asura.MessageBuilder
import com.game.asura.MessageField
import com.game.asura.MessageType

class PlayGameRequestOut : OutMessage {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.GAME_REQUEST.value)
        messageBuilder.add(MessageField.GAME_TYPE, GameType.NORMAL.value)

        messageBuilder.flip()
    }
}