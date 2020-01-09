package com.game.asura

import com.game.asura.card.Card
import kotlin.random.Random

class Match<P : Player>(val matchId: Int = Random.nextInt()) {


    private var matchTurn = 1
    private var currentPlayerTurn = ""
    //keep track of card drawn/hero for whole match to be able to search by card secondary id instantly
    private val cardCache: MutableMap<Int, Card> = HashMap()
    private val playerMap: MutableMap<String, P> = HashMap()

    fun getPlayer(key: String): P? {
        return playerMap[key]
    }

    fun addPlayer(key: String, value: P) {
        playerMap.putIfAbsent(key, value)
    }

    fun getMatchTurn(): Int {
        return matchTurn
    }

    fun increaseMatchTurn() {
        matchTurn++
    }

    fun addCardToCache(card: Card) {
        cardCache[card.getSecondayId()] = card
    }

    fun monsterAttack(secondaryId: Int, target: Int) {
        val attacker = cardCache[secondaryId] ?: return
        val target = cardCache[target] ?: return

        println("attacker/target logic here.")
    }

}