package com.game.asura.messagein

import com.game.asura.card.CardType
import com.game.asura.parsing.DecodedMessage

data class CardPlayedIn(val accountKey: String,
                        val cardPrimaryId: Int,
                        val cardSecondaryId: Int,
                        val cardTarget: Int?,
                        val boardPosition: Int?,
                        val cardType: CardType) : DecodedMessage