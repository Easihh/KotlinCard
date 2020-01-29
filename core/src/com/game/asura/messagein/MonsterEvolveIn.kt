package com.game.asura.messagein

import com.game.asura.card.CardType
import com.game.asura.parsing.DecodedMessage

class MonsterEvolveIn(
        val primaryCardId: Int,
        val secondaryCardId: Int,
        val firstMonsterId: Int,
        val secondMonsterId: Int,
        val boardPosition: Int,
        val cardCost: Int,
        val cardType: CardType,
        val attack:Int,
        val health:Int,
        val maxHealth:Int,
        val accountName:String) : DecodedMessage {

}