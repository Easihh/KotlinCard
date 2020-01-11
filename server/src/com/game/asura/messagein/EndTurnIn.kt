package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage

class EndTurnIn(val accountKey: String,
                val matchTurn: Int? = null) : DecodedMessage