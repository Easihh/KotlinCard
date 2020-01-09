package com.game.asura.card

interface HeroCard : Card {

    fun getAttack(): Int?

    fun getHealth(): Int
}