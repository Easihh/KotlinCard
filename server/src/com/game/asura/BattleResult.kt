package com.game.asura

import com.game.asura.card.Minion

class BattleResult(val defender: ServerPlayer) {
    private var defenderPlayerTookDmg = false
    val participant: MutableList<Minion> = ArrayList()
    fun defenderTakeDamage() {
        defenderPlayerTookDmg = true
    }

    fun defenderWasDamaged(): Boolean {
        return defenderPlayerTookDmg
    }

    fun addParticipant(card: Minion) {
        participant.add(card)
    }
}