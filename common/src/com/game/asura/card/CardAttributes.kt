package com.game.asura.card

class CardAttributes(private val attributes: Map<String, Any?> = HashMap()) {
    val attack: Int by attributes
    val health: Int by attributes
    val maxHealth: Int by attributes
}