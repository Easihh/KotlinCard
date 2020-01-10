package com.game.asura

class ServerHero(val primary: Int,
                 val secondaryId: Int) : BaseHero() {

    fun updateMana(cost: Int) {
        currentPlayerMana -= cost
    }

    fun takeDmg(dmg: Int) {
        playerLife -= dmg
    }

    override fun getPrimaryId(): Int {
        return primary
    }

    override fun getSecondayId(): Int {
        return secondaryId
    }
}