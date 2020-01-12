package com.game.asura.card

interface Minion {

    fun getAttack(): Int?

    fun getHealth(): Int

    fun getMaxHealth(): Int

    fun takeDamage(dmg: Int)
}