package com.game.asura.messagein

import com.game.asura.card.CardType
import com.game.asura.parsing.DecodedMessage

class CardDrawnIn( val primaryId: Int,
                   val secondaryId: Int,
                   val cardCost: Int,
                   val cardType: CardType) : DecodedMessage