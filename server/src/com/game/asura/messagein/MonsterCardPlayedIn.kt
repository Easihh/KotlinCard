package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage

data class MonsterCardPlayedIn(val accountKey: String,
                               val cardPrimaryId: Int,
                               val cardSecondaryId: Int,
                               val boardPosition: Int) : DecodedMessage