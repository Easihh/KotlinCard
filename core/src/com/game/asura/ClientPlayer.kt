package com.game.asura

class ClientPlayer(val playerName: String,
                   val heroPower: HeroPower,
                   primary: Int,
                   secondary: Int) {


    val boardManager = BoardManager<DrawableCard>(create = { INVALID_SPELL_CARD })
    val handManager = HandManager()
    val heroPlayer = ClientHeroCard(primary, secondary)

    var currentMana: Int = 0
    var maxMana: Int = 10
}