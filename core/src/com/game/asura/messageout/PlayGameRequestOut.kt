package com.game.asura.messageout

import com.game.asura.*

class PlayGameRequestOut : OutMessage {

    override fun build(messageBuilder: MessageBuilder) {
        messageBuilder.add(MessageField.MESSAGE_TYPE, MessageType.GAME_REQUEST.value)
        messageBuilder.add(MessageField.GAME_TYPE, GameType.NORMAL.value)
        messageBuilder.add(MessageField.END_MESSAGE, END_MESSAGE_DELIMITER)
        messageBuilder.flip()
    }
}