package com.game.asura.message.data

import com.game.asura.card.CardType

data class CardDrawnData(val primaryId: Int?,
                         val secondaryId: Int?,
                         val cardCost: Int?,
                         val cardType: CardType?,
                         val attack: Int?,
                         val health: Int?,
                         val maxHealth: Int?)