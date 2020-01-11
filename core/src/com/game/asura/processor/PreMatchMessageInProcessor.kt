package com.game.asura.processor

import com.game.asura.MAX_PLAYER_PER_MATCH
import com.game.asura.MatchHeroInfo
import com.game.asura.messagein.MatchInfoIn
import com.game.asura.parsing.DecodedMessage

class PreMatchMessageInProcessor(val toMatchScreen: (MatchHeroInfo) -> Unit) {

    private val matchHeroesInfos = MatchHeroInfo()
    fun onMessage(message: DecodedMessage) {
        when (message) {
            is MatchInfoIn -> {
                matchHeroesInfos.addHeroInfo(message.accountName, message)
                if (matchHeroesInfos.size() == MAX_PLAYER_PER_MATCH) {
                    toMatchScreen(matchHeroesInfos)
                }
            }
            else -> {
                println("Unable to process message:$message missing logic.")
            }
        }
    }
}