package com.game.asura.card

interface Minion : Card {

    fun getAttack(): Int

    fun getHealth(): Int

    fun getMaxHealth(): Int

    fun takeDamage(dmg: Int)

    fun isAlive(): Boolean

    fun isSummonSick(): Boolean

    fun removeSummonSickness()
}