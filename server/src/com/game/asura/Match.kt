package com.game.asura

import com.game.asura.card.Card
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
    private val matchCardCache: MutableMap<Int, Card> = HashMap()

    fun getCurrentPlayerTurn(): ServerPlayer {
        return currentPlayerTurn
    }

    fun getPlayer(key: String): ServerPlayer? {
        return playerMap[key]
    }

    fun getMatchTurn(): Int {
        return matchTurn
    }


    fun addCardToCache(card: Card) {
        matchCardCache[card.getSecondayId()] = card
    }

    fun removeCardFromCache(card: Card) {
        matchCardCache.remove(card.getSecondayId())
    }

    fun getCard(secondaryId: Int): Card? {
        return matchCardCache[secondaryId]
    }

    fun processAttack(attackingPlayer: String): BattleResult? {
        val attackPlayer = getPlayer(attackingPlayer) ?: return null
        val defenderName = getOpponentName(attackingPlayer)
        val defenderPlayer = getPlayer(defenderName) ?: return null
        val bResult = BattleResult(defenderPlayer)
        var dmgToDefender = 0
        if (defenderPlayer.boardManager.boardIsEmpty()) {
            //defender has no minion on board, deal all dmg to player
            for (i in 0 until MAX_BOARD_SIZE) {
                val card = attackPlayer.boardManager.getCardByBoardIndex(i) ?: continue
                dmgToDefender += card.getAttack()
            }
            println("Dealing $dmgToDefender damage to player $defenderName")
            defenderPlayer.playerLifePoint -= dmgToDefender
            bResult.defenderTakeDamage()
            return bResult
        }

        for (i in 0 until MAX_BOARD_SIZE) {
            val attackerMinion = attackPlayer.boardManager.getCardByBoardIndex(i) ?: continue
            val defenderMinion = defenderPlayer.boardManager.getCardByBoardIndex(i)
            if (defenderMinion != null) {
                attackerMinion.takeDamage(defenderMinion.getAttack())
                if (!attackerMinion.isAlive()) {
                    attackPlayer.boardManager.removeCard(attackerMinion)
                }
                defenderMinion.takeDamage(attackerMinion.getAttack())
                if (!defenderMinion.isAlive()) {
                    defenderPlayer.boardManager.removeCard(defenderMinion)
                }
                bResult.addParticipant(attackerMinion)
                bResult.addParticipant(defenderMinion)
            } else {
                dmgToDefender += attackerMinion.getAttack()
            }
        }
        if (dmgToDefender > 0) {
            println("Dealing $dmgToDefender damage to player $defenderName")
            defenderPlayer.playerLifePoint -= dmgToDefender
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
            calculatePlayerPhase(player2)
            return
        }
        currentPlayerTurn = player1
        calculatePlayerPhase(player1)
    }

    private fun calculatePlayerPhase(serverPlayer: ServerPlayer) {
        if (serverPlayer.boardManager.boardIsEmpty()) {
            serverPlayer.currentPhase = Phase.MAIN
            return
        }
        serverPlayer.currentPhase = Phase.ATTACK
    }

}