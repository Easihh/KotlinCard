package com.game.asura.messagein

import com.game.asura.DecodedMessage
import com.game.asura.GameType

class HeroPowerIn(val accountKey: String,
                  val matchId: Int,
                  val target: Int?) : DecodedMessage