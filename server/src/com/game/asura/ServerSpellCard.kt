package com.game.asura

import com.game.asura.card.BaseCard
import com.game.asura.card.CardType
import kotlin.random.Random

val INVALID_SPELL_CARD = ServerSpellCard(-1, -1, 99, CardType.INVALID)

class ServerSpellCard(primaryId: Int,
                      secondaryId: Int = Random.nextInt(),
                      cardCost: Int = 0,
                      cardType: CardType) : BaseCard(primaryId, secondaryId, cardCost, cardType)