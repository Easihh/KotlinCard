package com.game.asura

import com.game.asura.card.BaseCard
import com.game.asura.card.CardType
import kotlin.random.Random

class Match(val matchId: Int = Random.nextInt()) {


    private var matchTurn = 1
    private var currentPlayerTurn = ""
    //keep track of card drawn/hero for whole match to be able to search by card secondary id instantly
    private val matchCardCache: MutableMap<Int, BaseCard> = HashMap()
    private val playerMap: MutableMap<String, ServerPlayer> = HashMap()

    fun getPlayer(key: String): ServerPlayer? {
        return playerMap[key]
    }

    fun addPlayer(key: String, value: ServerPlayer) {
        playerMap.putIfAbsent(key, value)
    }

    fun getMatchTurn(): Int {
        return matchTurn
    }

    fun increaseMatchTurn() {
        matchTurn++
    }

    fun addCardToCache(card: BaseCard) {
        matchCardCache[card.getSecondayId()] = card
    }

    fun removeCardFromCache(card: BaseCard) {
        matchCardCache.remove(card.getSecondayId())
    }

    fun getCard(secondaryId: Int): BaseCard? {
        return matchCardCache[secondaryId]
    }

    fun processAttack(attackingPlayer: String) {
        val attackPlayer = getPlayer(attackingPlayer) ?: return
        val defenderName = playerMap.keys.stream().filter { s -> s != attackingPlayer }.findFirst().get()
        val defenderPlayer = getPlayer(defenderName) ?: return
        if (defenderPlayer.boardManager.boardIsEmpty()) {
            //defender has no minion on board, deal all dmg to player
            var dmg = 0
            for (i in 0 until MAX_BOARD_SIZE) {
                val card = attackPlayer.boardManager.getCardByBoardIndex(i) as ServerMinionCard
                if (card.getCardType() != CardType.INVALID) {
                    dmg += card.getAttack()
                }
            }
            println("Dealing $dmg damage to player $defenderName")
            return
        }
    }

}