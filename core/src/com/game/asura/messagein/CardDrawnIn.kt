package com.game.asura.messagein

import com.game.asura.CardType
import com.game.asura.DecodedMessage

class CardDrawnIn( val primaryId: Int,
                   val secondaryId: Int,
                   val cardCost: Int,
                   val cardType: CardType) : DecodedMessage