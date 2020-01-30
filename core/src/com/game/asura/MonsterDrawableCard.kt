package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.game.asura.card.CardType
import com.game.asura.card.Minion
import com.game.asura.messaging.MessageField
import kotlin.random.Random

abstract class MonsterDrawableCard(texture: Texture,
                                   primaryId: Int,
                                   secondaryId: Int = Random.nextInt(),
                                   cardCost: Int,
                                   cardType: CardType,
                                   private var attack: Int,
                                   private var health: Int,
                                   private var maxHealth: Int) : DrawableCard(texture,primaryId, secondaryId, cardCost, cardType), Minion {

    private val updateFncMap: MutableMap<MessageField, (ChangedField) -> Unit> = HashMap()
    protected var summonSickness = true

    init {

        updateFncMap[MessageField.CARD_HEALTH] = { health = it.value as Int }
        updateFncMap[MessageField.SUMMON_ILLNESS] = { summonSickness = it.value as Boolean }
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

    override fun getAttack(): Int {
        return attack
    }

    override fun takeDamage(dmg: Int) {
        health -= dmg
    }

    override fun isAlive(): Boolean {
        return health > 0
    }
}