package com.game.asura.processor

import com.game.asura.messageout.OutMessage

class PreMatchMessageOutProcessor(val sendFnc: (OutMessage) -> Unit) {

    fun onMessage(message: OutMessage) {
        sendFnc(message)
    }
}