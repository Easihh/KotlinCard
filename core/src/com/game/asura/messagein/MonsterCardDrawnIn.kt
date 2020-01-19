package com.game.asura.messagein

import com.game.asura.card.CardType
import com.game.asura.parsing.DecodedMessage

class MonsterCardDrawnIn(val primaryId: Int,
                         val secondaryId: Int,
                         val cardCost: Int,
                         val cardType: CardType,
                         val attack: Int?,
                         val health: Int,
                         val maxHealth: Int) : DecodedMessage