package com.game.asura.card

import kotlinx.serialization.Serializable

@Serializable
data class CardInfo(val id: Int,
                    val name: String,
                    val cost: Int,
                    val cardType: CardType,
                    val attack: Int? = null,
                    val health: Int? = null,
                    val maxHealth: Int? = null)