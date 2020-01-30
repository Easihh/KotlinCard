package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage

data class SpellCardPlayedIn(val accountKey: String,
                             val cardPrimaryId: Int,
                             val cardSecondaryId: Int,
                             val cardTarget: Int?) : DecodedMessage