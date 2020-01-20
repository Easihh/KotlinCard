package com.game.asura

import com.game.asura.card.BaseCard
import com.game.asura.card.Minion
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

    fun getCardOwner(secondaryId: Int): ServerPlayer? {
        for (player in playerMap.values) {
            if (player.boardManager.cardIsPresentOnBoard(secondaryId) ||
                    player.heroPlayer.getSecondayId() == secondaryId ||
                    player.handManager.cardIsInHand(secondaryId)) {
                return player
            }
        }
        return null
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

    fun addCardToCache(card: BaseCard) {
        matchCardCache[card.getSecondayId()] = card
    }

    fun removeCardFromCache(card: BaseCard) {
        matchCardCache.remove(card.getSecondayId())
    }

    fun getCard(secondaryId: Int): BaseCard? {
        return matchCardCache[secondaryId]
    }

    fun monsterAttack(attacker: Minion, defender: Minion) {
        attacker.takeDamage(defender.getAttack())
        defender.takeDamage(attacker.getAttack())
    }

}