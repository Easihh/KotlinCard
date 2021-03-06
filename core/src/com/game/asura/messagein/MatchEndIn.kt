package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage
import com.game.asura.GameType
import com.game.asura.MatchResult
import com.game.asura.card.CardType

data class MatchEndIn(val accountName: String,
                      val matchResult: MatchResult) : DecodedMessage