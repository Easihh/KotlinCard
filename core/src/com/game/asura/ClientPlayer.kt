package com.game.asura

import com.game.asura.card.AllCard

class ClientPlayer(val playerName: String, val heroPower: HeroPower) {


    val boardManager = BoardManager<DrawableCard>(create = { INVALID_CLIENT_CARD })
    val handManager = HandManager()
    val heroPlayer = ClientHero(AllCard.MAGE_HERO.id)
    private var currentMatchId: Int? = null


    fun setMatchId(matchId: Int) {
        currentMatchId = matchId
    }

    fun getCurrentMatchId(): Int? {
        return currentMatchId
    }


    fun update(changes: List<ChangedField>) {
        for (change in changes) {
            heroPlayer.updateField(change)
        }
    }
}