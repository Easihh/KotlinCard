package com.game.asura

import com.game.asura.card.CardType
import kotlin.random.Random

abstract class HeroPower(primaryId: Int,
                         secondaryId: Int = Random.nextInt(),
                         cardCost: Int,
                         cardType: CardType) : DrawableCard(primaryId,secondaryId,cardCost,cardType) {

    abstract fun isActive(): Boolean

    abstract fun activate()

    abstract fun deactivate()
}