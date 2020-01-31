package com.game.asura

import com.game.asura.card.CardType
import com.game.asura.card.Minion

class BoardManager<T : Minion>(private val create: () -> T) {

    private val playerBoard: MutableList<T> = ArrayList()

    init {
        for (x in 0 until MAX_BOARD_SIZE) {
            playerBoard.add(create())
        }
    }

    fun boardIsEmpty(): Boolean {
        var count = 0
        for (index in 0 until MAX_BOARD_SIZE) {
            if (playerBoard[index].getCardType() != CardType.INVALID) {
                count++
            }
        }
        return count == 0
    }

    fun updatePlayerBoard(card: T, boardIndex: Int) {
        playerBoard[boardIndex] = card
    }

    fun getCardByBoardIndex(boardIndex: Int): T {
        return playerBoard[boardIndex]
    }

    fun removeCard(target: T) {
        //board should always  contains 5 space(invalid obj for no card in that indx)
        // otherwise player sending monster placement will have issue.
        for (i in 0 until MAX_BOARD_SIZE) {
            if (playerBoard[i].getSecondayId() == target.getSecondayId()) {
                playerBoard[i] = create()
            }
        }
    }

    fun findDuplicate(primaryId: Int): List<DuplicatedCard> {
        val dupeList: MutableList<DuplicatedCard> = ArrayList()
        for (i in 0 until MAX_BOARD_SIZE) {
            if (playerBoard[i].getPrimaryId() == primaryId) {
                dupeList.add(DuplicatedCard(playerBoard[i], i))
            }
        }
        return dupeList
    }

    //merge both card and move the resulting card at the position of the first played duplicated card
    fun mergeCard(toMerge: List<DuplicatedCard>, evolve: T, toReplace: T, lastPlayedId: Int) {
        for (card in toMerge) {
            if (card.dupeCard.getSecondayId() == lastPlayedId) {
                playerBoard[card.boardIdx] = toReplace
            } else {
                playerBoard[card.boardIdx] = evolve
            }
        }

    }

    fun updateSummonIllness(): List<T> {
        val updated: MutableList<T> = ArrayList()
        for (i in 0 until MAX_BOARD_SIZE) {
            if (playerBoard[i].isSummonSick()) {
                playerBoard[i].removeSummonSickness()
                updated.add(playerBoard[i])
            }
        }
        return updated
    }
}