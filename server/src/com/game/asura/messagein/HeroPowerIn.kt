package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage

class HeroPowerIn(val accountKey: String,
                  val matchId: Int,
                  val target: Int?) : DecodedMessage