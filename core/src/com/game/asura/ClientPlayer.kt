package com.game.asura

class ClientPlayer(val playerName: String) {


    val boardManager = BoardManager<MonsterDrawableCard>()
    val handManager = HandManager<DrawableCard>()

    var playerLifePoint: Int = 30
    var currentMana: Int = 0
    var maxMana: Int = 10
    var currentPhase: Phase = Phase.MAIN
    var myTurn = false
    var selectedCard: DrawableCard? = null
}