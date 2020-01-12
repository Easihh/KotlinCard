package com.game.asura

class ClientPlayer(val playerName: String,
                   val heroPower: HeroPower,
                    primary: Int,
                    secondary: Int) {


    val boardManager = BoardManager<DrawableCard>(create = { INVALID_SPELL_CARD })
    val handManager = HandManager()
    val heroPlayer = ClientHeroCard(primary, secondary)


    fun update(changes: List<ChangedField>) {
        for (change in changes) {
            heroPlayer.updateField(change)
        }
    }
}