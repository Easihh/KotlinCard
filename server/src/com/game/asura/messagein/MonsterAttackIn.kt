package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage

class MonsterAttackIn(val accountKey: String,
                      val matchId: Int,
                      val primaryId:Int,
                      val secondaryId:Int,
                      val target: Int) : DecodedMessage