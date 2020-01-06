package com.game.asura.messageout

import com.game.asura.messaging.Message
import com.game.asura.messaging.MessageBuilder

interface OutMessage : Message {

    fun build(messageBuilder: MessageBuilder)
}