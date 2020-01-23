package com.game.asura.message.data

import com.game.asura.card.CardType

class MonsterEvolveData(val primaryCardId: Int?,
                        val secondaryCardId: Int?,
                        val cardCost: Int?,
                        val cardType: CardType?,
                        val firstMonsterId: Int?,
                        val secondMonsterId: Int?,
                        val boardPosition: Int?,
                        val attack:Int?,
                        val health:Int?,
                        val maxHealth:Int?)