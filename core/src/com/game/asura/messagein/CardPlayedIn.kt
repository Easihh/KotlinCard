package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage

data class CardPlayedIn(val primaryId: Int,
                        val secondaryId: Int,
                        val accountName: String) : DecodedMessage