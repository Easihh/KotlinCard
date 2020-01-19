package com.game.asura

import com.game.asura.card.CardType

class ClientPlayer(val playerName: String,
                   val heroPower: HeroPower,
                   primary: Int,
                   secondary: Int) {


    val boardManager = BoardManager<DrawableCard>(create = { INVALID_SPELL_CARD })
    val handManager = HandManager()
    val heroPlayer = ClientHeroCard(primary, secondary, 0, CardType.HERO, null, 30, 30)

    var currentMana: Int = 0
    var maxMana: Int = 10
}