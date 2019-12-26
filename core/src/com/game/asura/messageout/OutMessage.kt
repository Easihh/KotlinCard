package com.game.asura.messageout

import com.game.asura.Message
import com.game.asura.MessageBuilder

interface OutMessage : Message {

    fun build(messageBuilder: MessageBuilder)
}