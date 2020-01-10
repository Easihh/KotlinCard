package com.game.asura.processor

import com.game.asura.messagein.MatchInfoIn
import com.game.asura.parsing.DecodedMessage

class PreMatchMessageInProcessor {

    fun onMessage(message: DecodedMessage) {
        when (message) {
            is MatchInfoIn -> {
                val matchId = message.matchId ?: return
                //player.setMatchId(matchId)
            }
            else -> {
                println("Unable to process message:$message missing logic.")
            }
        }
    }
}