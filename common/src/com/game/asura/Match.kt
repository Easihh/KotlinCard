package com.game.asura

import kotlin.random.Random

class Match<Player>(val matchId: Int = Random.nextInt(),
                    val gameType: GameType) {


    private var matchTurn = 1
    private var currentPlayerTurn = 0
    private val playerMap: MutableMap<String, Player> = HashMap()

    fun getPlayer(key: String): Player? {
        return playerMap[key]
    }

    fun addPlayer(key: String, value: Player) {
        playerMap.putIfAbsent(key, value)
    }

    fun getMatchTurn(): Int {
        return matchTurn
    }

    fun increaseMatchTurn() {
        matchTurn++
    }

}