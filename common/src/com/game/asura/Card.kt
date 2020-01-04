package com.game.asura

interface Card {

    fun getPrimaryId(): Int

    fun getSecondayId(): Int

    fun getCost(): Int

    fun getCardType(): CardType

    fun getEffect(): List<CardEffect>

    fun getAttack(): Int?

    fun getHealth(): Int?
}