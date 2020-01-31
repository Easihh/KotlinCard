package com.game.asura

import com.game.asura.card.Minion

class BoardManager<T : Minion> {

    private val playerBoard: MutableList<T?> = mutableListOf(null, null, null, null, null)

    fun boardIsEmpty(): Boolean {
        var count = 0
        for (index in 0 until MAX_BOARD_SIZE) {
            if (playerBoard[index] != null) {
                count++
            }
        }
        return count == 0
    }

    fun updatePlayerBoard(card: T, boardIndex: Int) {
        playerBoard[boardIndex] = card
    }

    fun getCardByBoardIndex(boardIndex: Int): T? {
        return playerBoard[boardIndex]
    }

    fun removeCard(target: T) {
        //board should always  contains 5 space(invalid obj for no card in that indx)
        // otherwise player sending monster placement will have issue.
        for (i in 0 until MAX_BOARD_SIZE) {
            val card = playerBoard[i] ?: continue
            if (card.getSecondayId() == target.getSecondayId()) {
                playerBoard[i] = null
            }
        }
    }

    fun findDuplicate(primaryId: Int): List<DuplicatedCard> {
        val dupeList: MutableList<DuplicatedCard> = ArrayList()
        for (i in 0 until MAX_BOARD_SIZE) {
            val dupeCard = playerBoard[i] ?: continue
            if (dupeCard.getPrimaryId() == primaryId) {
                dupeList.add(DuplicatedCard(dupeCard, i))
            }
        }
        return dupeList
    }

    //merge both card and move the resulting card at the position of the first played duplicated card
    fun mergeCard(toMerge: List<DuplicatedCard>, evolve: T, lastPlayedId: Int) {
        for (card in toMerge) {
            if (card.dupeCard.getSecondayId() == lastPlayedId) {
                playerBoard[card.boardIdx] = null
            } else {
                playerBoard[card.boardIdx] = evolve
            }
        }

    }

    fun updateSummonIllness(): List<T> {
        val updated: MutableList<T> = ArrayList()
        for (i in 0 until MAX_BOARD_SIZE) {
            val card = playerBoard[i] ?: continue
            if (card.isSummonSick()) {
                card.removeSummonSickness()
                updated.add(card)
            }
        }
        return updated
    }
}