package com.game.asura.processor

import com.game.asura.messagein.MatchStartIn
import com.game.asura.parsing.DecodedMessage

class PreMatchMessageInProcessor(val toMatchScreen: (MatchStartIn) -> Unit) : MessageProcessor<DecodedMessage> {

    override fun onMessage(msg: DecodedMessage) {
        when (msg) {
            is MatchStartIn -> {
                toMatchScreen(msg)
            }
            else -> {
                println("Unable to process message:$msg missing logic.")
            }
        }
    }
}