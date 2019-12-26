package com.game.asura.processor

import com.game.asura.messageout.OutMessage

class MessageOutProcessor(val sendFnc: (OutMessage) -> Unit) {

    fun onMessage(message: OutMessage) {
        sendFnc(message)
    }
}