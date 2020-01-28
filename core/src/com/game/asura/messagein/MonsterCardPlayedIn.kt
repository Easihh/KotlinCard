package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage

data class MonsterCardPlayedIn(val primaryId: Int,
                               val secondaryId: Int,
                               val accountName: String,
                               val attack: Int,
                               val health: Int,
                               val maxHealth: Int,
                               val boardIndx:Int,
                               val cardCost:Int) : DecodedMessage