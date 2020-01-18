package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.game.asura.card.AllCard
import com.game.asura.card.CardType
import com.game.asura.card.Minion
import com.game.asura.messaging.MessageField
import kotlin.random.Random

abstract class MonsterDrawableCard(primaryId: Int,
                                   secondaryId: Int = Random.nextInt(),
                                   cardCost: Int,
                                   cardType: CardType) : DrawableCard(primaryId, secondaryId, cardCost, cardType), Minion {

    protected val updateFncMap: MutableMap<MessageField, (ChangedField) -> Unit> = HashMap()
    private val attack: Int = AllCard.getCard(primaryId).attributes.attack
    private var health: Int = AllCard.getCard(primaryId).attributes.health
    private var maxHealth: Int = AllCard.getCard(primaryId).attributes.maxHealth

    init {

        updateFncMap[MessageField.CARD_HEALTH] = { health = it.value as Int }
        updateFncMap[MessageField.CARD_MAX_HEALTH] = { maxHealth = it.value as Int }
    }

    fun update(changedFieldLst: List<ChangedField>) {
        for (changedField in changedFieldLst) {
            updateFncMap[changedField.field]?.invoke(changedField)
        }
    }

    override fun getHealth(): Int {
        return health
    }

    override fun getMaxHealth(): Int {
        return maxHealth
    }

    override fun getAttack(): Int? {
        return attack
    }

    override fun takeDamage(dmg: Int) {
        health -= dmg
    }

    override fun isAlive(): Boolean {
        return health > 0
    }
}