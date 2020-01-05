package com.game.asura

interface CardCache {

    fun add(card: DrawableCard)

    fun remove(card: DrawableCard)

    fun getCard(key: Int): DrawableCard?
}