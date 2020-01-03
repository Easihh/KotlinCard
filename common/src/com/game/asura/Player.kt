package com.game.asura

abstract class Player(private val playerName: String) {

    val handManager = HandManager()
    protected var playerLife: Int = 30
    protected var maxPlayerLife: Int = 30
    protected var currentPlayerArmor: Int = 0
    protected var currentPlayerMana: Int = 10
    protected var maxPlayerMana: Int = 10


    fun getPlayerName(): String {
        return playerName
    }

    fun getPlayerMana(): Int {
        return currentPlayerMana
    }

    fun getPlayerMaxMana(): Int {
        return maxPlayerMana
    }

    fun getCurrentPlayerLife(): Int {
        return playerLife
    }


    fun getPlayerMaxLife(): Int {
        return maxPlayerLife
    }


}