package com.game.asura

import com.game.asura.card.CardType

class ClientPlayer(val playerName: String,
                   primary: Int,
                   secondary: Int) {


    val boardManager = BoardManager<DrawableCard>(create = { INVALID_SPELL_CARD })
    val handManager = HandManager<DrawableCard>()
    val heroPlayer = ClientHeroCard(primary, secondary, 0, CardType.HERO, -1, 30, 30)

    var playerLifePoint: Int = 30
    var currentMana: Int = 0
    var maxMana: Int = 10
}