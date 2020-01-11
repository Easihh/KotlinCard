package com.game.asura

import com.game.asura.card.Card
import kotlin.random.Random

class Match(val matchId: Int = Random.nextInt()) {


    private var matchTurn = 1
    private var currentPlayerTurn = ""
    //keep track of card drawn/hero for whole match to be able to search by card secondary id instantly
    private val matchCardCache: MutableMap<Int, Card> = HashMap()
    private val playerMap: MutableMap<String, ServerPlayer> = HashMap()

    fun getPlayer(key: String): ServerPlayer? {
        return playerMap[key]
    }

    fun addPlayer(key: String, value: ServerPlayer) {
        playerMap.putIfAbsent(key, value)
        //add "Hero" player as its also a card to be retrieved on attack/defense
        addCardToCache(value.heroPlayer)
    }

    fun getMatchTurn(): Int {
        return matchTurn
    }

    fun increaseMatchTurn() {
        matchTurn++
    }

    fun addCardToCache(card: Card) {
        matchCardCache[card.getSecondayId()] = card
    }

    fun getCard(secondaryId: Int): Card? {
        return matchCardCache[secondaryId]
    }

    fun monsterAttack(secondaryId: Int, target: Int) {
        val attacker = matchCardCache[secondaryId] ?: return
        val target = matchCardCache[target] ?: return

        println("attacker/target logic here.")
    }

}