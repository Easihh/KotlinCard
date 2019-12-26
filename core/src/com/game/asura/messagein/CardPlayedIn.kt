package com.game.asura.messagein

import com.game.asura.DecodedMessage

data class CardPlayedIn(val primaryId: Int,
                        val secondaryId: Int) : DecodedMessage