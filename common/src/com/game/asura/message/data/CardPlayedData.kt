package com.game.asura.message.data

import com.game.asura.card.CardType

data class CardPlayedData(val cardPrimaryId: Int?,
                          val cardSecondaryId: Int?,
                          val cardTarget: Int?,
                          val accountName: String?,
                          val cardType: CardType?,
                          val boardIdx: Int?)