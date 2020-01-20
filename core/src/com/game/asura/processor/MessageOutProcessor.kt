package com.game.asura.processor

import com.game.asura.messageout.OutMessage

class MessageOutProcessor(val sendFnc: (OutMessage) -> Unit):MessageProcessor<OutMessage> {

    override fun onMessage(msg: OutMessage) {
        sendFnc(msg)
    }
}