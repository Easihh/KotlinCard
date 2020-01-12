package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.game.asura.card.CardType
import com.game.asura.card.Minion
import com.game.asura.messaging.MessageField

class ClientHeroCard(primaryId: Int,
                     secondaryId: Int,
                     cardCost: Int = 0,
                     cardType: CardType = CardType.HERO) : DrawableCard(primaryId, secondaryId, cardCost, cardType), Minion {

    private var attack: Int? = null
    private var health: Int = 30
    private var maxHealth: Int = 30
    private var currentMana: Int = 0
    private var maxMana: Int = 10
    private val updateFncMap: MutableMap<MessageField, (ChangedField) -> Unit> = HashMap()
    private val playerActor: BoardCard

    init {

        val texture = Texture("core/assets/hero.png")
        playerActor = BoardCard(texture, getSecondayId())

        updateFncMap[MessageField.CARD_HEALTH] = { health = it.value as Int }
        updateFncMap[MessageField.CARD_MAX_HEALTH] = { maxHealth = it.value as Int }
        updateFncMap[MessageField.PLAYER_CURRENT_MANA] = { currentMana = it.value as Int }
        updateFncMap[MessageField.PLAYER_MAX_MANA] = { maxMana = it.value as Int }
    }

    fun updateField(changedField: ChangedField) {
        updateFncMap[changedField.field]?.invoke(changedField)
    }

    override fun getActor(): Actor {
        return playerActor
    }

    override fun getHealth(): Int {
        return health
    }

    override fun getMaxHealth(): Int {
        return maxHealth
    }

    fun getCurrentMana(): Int {
        return currentMana
    }

    fun getMaxMana(): Int {
        return maxMana
    }

    override fun getAttack(): Int? {
        return attack
    }

    override fun transformActor(texture: Texture) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun takeDamage(dmg: Int) {
        health -= dmg
    }

}