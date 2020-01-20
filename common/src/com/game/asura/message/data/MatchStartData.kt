package com.game.asura.message.data

data class MatchStartData(val accountName: String?,
                          val enemyAccountName: String?,
                          val primaryHeroId: Int?,
                          val secondaryHeroId: Int?,
                          val enemyPrimaryHeroId: Int?,
                          val enemySecondaryHeroId: Int?)