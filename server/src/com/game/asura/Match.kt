package com.game.asura

import com.game.asura.card.BaseCard
import com.game.asura.card.CardType
import kotlin.random.Random

class Match(private val player1: ServerPlayer,
            private val player2: ServerPlayer,
            val matchId: Int = Random.nextInt()) {

    private var currentPlayerTurn = player1
    private val playerMap: MutableMap<String, ServerPlayer> = HashMap()

    init {
        playerMap[player1.playerName] = player1
        playerMap[player2.playerName] = player2
    }

    private var matchTurn = 1

    //keep track of card drawn/added for whole match to be able to search by card secondary id instantly
    private val matchCardCache: MutableMap<Int, BaseCard> = HashMap()

    fun getCurrentPlayerTurn(): ServerPlayer {
        return currentPlayerTurn
    }

    fun getPlayer(key: String): ServerPlayer? {
        return playerMap[key]
    }

    fun getMatchTurn(): Int {
        return matchTurn
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

    fun processAttack(attackingPlayer: String): BattleResult? {
        val attackPlayer = getPlayer(attackingPlayer) ?: return null
        val defenderName = getOpponentName(attackingPlayer)
        val defenderPlayer = getPlayer(defenderName) ?: return null
        val bResult = BattleResult(defenderPlayer)
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
            defenderPlayer.playerLifePoint -= dmg
            bResult.defenderTakeDamage()
        }
        return bResult
    }

    fun getOpponentName(playerName: String): String {
        if (player1.playerName == playerName) {
            return player2.playerName
        }
        return player1.playerName
    }

    fun setPlayerNextPhase(playerName: String, nextPhase: Phase) {
        if (player1.playerName == playerName) {
            player1.currentPhase = nextPhase
            return
        }
        player2.currentPhase = nextPhase
    }

    fun endTurn() {
        matchTurn++
        if (currentPlayerTurn.playerName == player1.playerName) {
            currentPlayerTurn = player2
            player2.currentPhase = Phase.MAIN
            return
        }
        currentPlayerTurn = player1
    }

}