package com.game.asura.messagein

import com.game.asura.DecodedMessage
import com.game.asura.GameType

data class MatchInfoIn(val matchId: Int?, val gameType: GameType) : DecodedMessage