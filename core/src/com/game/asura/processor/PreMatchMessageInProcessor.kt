package com.game.asura.processor

import com.game.asura.messagein.MatchInfoIn
import com.game.asura.parsing.DecodedMessage

class PreMatchMessageInProcessor(val toMatchScreen: (MatchInfoIn) -> Unit) {

    fun onMessage(message: DecodedMessage) {
        when (message) {
            is MatchInfoIn -> {
                toMatchScreen(message)
            }
            else -> {
                println("Unable to process message:$message missing logic.")
            }
        }
    }
}