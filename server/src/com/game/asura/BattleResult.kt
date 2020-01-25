package com.game.asura

class BattleResult(val defender: ServerPlayer) {
    private var defenderPlayerTookDmg = false
    fun defenderTakeDamage() {
        defenderPlayerTookDmg = true
    }

    fun defenderWasDamaged(): Boolean {
        return defenderPlayerTookDmg
    }

}