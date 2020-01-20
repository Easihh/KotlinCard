package com.game.asura.processor

import com.game.asura.messagein.MatchStartIn
import com.game.asura.parsing.DecodedMessage

class PreMatchMessageInProcessor(val toMatchScreen: (MatchStartIn) -> Unit) {

    fun onMessage(message: DecodedMessage) {
        when (message) {
            is MatchStartIn -> {
                toMatchScreen(message)
            }
            else -> {
                println("Unable to process message:$message missing logic.")
            }
        }
    }
}