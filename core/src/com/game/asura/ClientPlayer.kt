package com.game.asura

import com.game.asura.card.CardType

class ClientPlayer(val playerName: String) {


    val boardManager = BoardManager<MonsterDrawableCard>(create = {
        MinionCard(NULL_BOARD_CARDTEXTURE, -1,
                -1, 999, CardType.INVALID, -1, -1, -1)
    })
    val handManager = HandManager<DrawableCard>()

    var playerLifePoint: Int = 30
    var currentMana: Int = 0
    var maxMana: Int = 10
    var currentPhase: Phase = Phase.MAIN
    var myTurn = false
}