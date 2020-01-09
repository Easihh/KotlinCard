package com.game.asura

class ServerHero(primaryId: Int) : BaseHero(primaryId) {

    fun updateMana(cost: Int) {
        currentPlayerMana -= cost
    }

    fun takeDmg(dmg: Int) {
        playerLife -= dmg
    }
}