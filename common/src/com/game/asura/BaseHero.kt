package com.game.asura

import com.game.asura.card.CardEffect
import com.game.asura.card.CardType
import com.game.asura.card.HeroCard

abstract class BaseHero : HeroCard {

    protected var playerLife: Int = 30
    protected var maxPlayerLife: Int = 30
    protected var currentPlayerArmor: Int = 0
    protected var currentPlayerMana: Int = 10
    protected var maxPlayerMana: Int = 10
    protected var attackPower: Int? = null



    override fun getCost(): Int {
        return 0
    }

    override fun getCardType(): CardType {
        return CardType.HERO
    }

    override fun getEffect(): List<CardEffect> {
        return emptyList()
    }

    override fun getAttack(): Int? {
        return attackPower
    }

    override fun getHealth(): Int {
        return playerLife
    }

    fun getCurrentMana(): Int {
        return currentPlayerMana
    }

    fun getPlayerMaxMana(): Int {
        return maxPlayerMana
    }
}