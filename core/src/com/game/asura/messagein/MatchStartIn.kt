package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage
import com.game.asura.GameType
import com.game.asura.card.CardType

data class MatchStartIn(val accountName: String,
                        val enemyName: String,
                        val primaryHeroId: Int,
                        val secondaryHeroId: Int,
                        val enemyPrimaryHeroId: Int,
                        val enemySecondaryHeroId: Int) : DecodedMessage