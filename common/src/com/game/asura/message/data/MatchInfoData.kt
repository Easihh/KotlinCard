package com.game.asura.message.data

import com.game.asura.card.CardType

data class MatchInfoData(val accountName: String?,
                         val enemyAccountName: String?,
                         val primaryHeroId: Int?,
                         val secondaryHeroId: Int?,
                         val cardType: CardType?)