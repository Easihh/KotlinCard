package com.game.asura

class ClientPlayer(playerName: String, val heroPower: HeroPower) : Player(playerName) {

    private val updateFncMap: MutableMap<MessageField, (ChangedField) -> Unit> = HashMap()

    //Server dont need below
    init {
        updateFncMap[MessageField.PLAYER_CURRENT_HEALTH] = { playerLife = it.value as Int }
        updateFncMap[MessageField.PLAYER_MAX_HEALTH] = { maxPlayerLife = it.value as Int }
        updateFncMap[MessageField.PLAYER_CURRENT_MANA] = { currentPlayerMana = it.value as Int }
        updateFncMap[MessageField.PLAYER_MAX_MANA] = { maxPlayerMana = it.value as Int }
    }

    fun update(changes: List<ChangedField>) {
        for (change in changes) {
            updateFncMap[change.field]?.invoke(change)
        }
    }
}