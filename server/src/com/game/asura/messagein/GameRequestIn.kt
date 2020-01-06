package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage
import com.game.asura.GameType

class GameRequestIn(val accountKey: String,
                    val gameType: GameType) : DecodedMessage