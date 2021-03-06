package com.game.asura

import com.game.asura.card.CardType
import kotlinx.serialization.Serializable

@Serializable
data class CardInfo(val id: Int,
                    val name: String,
                    val cost: Int,
                    val cardType: CardType,
                    val attack: Int? = null,
                    val health: Int? = null,
                    val maxHealth: Int? = null,
                    val evolveId: Int? = null,
                    val ability: MutableList<Int> = ArrayList())