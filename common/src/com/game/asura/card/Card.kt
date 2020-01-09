package com.game.asura.card

interface Card {

    fun getPrimaryId(): Int

    fun getSecondayId(): Int

    fun getCost(): Int

    fun getCardType(): CardType

    fun getEffect(): List<CardEffect>
}