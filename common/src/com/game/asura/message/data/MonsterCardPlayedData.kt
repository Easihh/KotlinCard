package com.game.asura.message.data

data class MonsterCardPlayedData(val cardPrimaryId: Int?,
                                 val cardSecondaryId: Int?,
                                 val accountName: String?,
                                 val attack: Int?,
                                 val health: Int?,
                                 val maxHealth: Int?,
                                 val boardIndx: Int?,
                                 val cardCost: Int?)