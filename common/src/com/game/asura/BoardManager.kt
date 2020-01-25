package com.game.asura

import com.game.asura.card.Card
import com.game.asura.card.CardType
import java.util.stream.Collectors

class BoardManager<T : Card>(private val create: () -> T) {

    enum class BoardPositionTilt {
        LEFT,
        RIGHT,
        NONE
    }

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

    fun getRightMostCardOnBoard(): Int {
        for (indx in MAX_BOARD_SIZE - 1 downTo 0) {
            if (playerBoard[indx].getCardType() != CardType.INVALID) {
                return indx
            }
        }
        //no board, return the mid position
        return MAX_BOARD_SIZE / 2
    }

    fun getLeftMostCardOnBoard(): Int {
        for (indx in 0 until MAX_BOARD_SIZE) {
            if (playerBoard[indx].getCardType() != CardType.INVALID) {
                return indx
            }
        }
        //no board, return the mid position
        return MAX_BOARD_SIZE / 2
    }

    fun updatePlayerBoard(card: T, boardIndex: Int) {
        playerBoard[boardIndex] = card
    }

    fun getCardByBoardIndex(boardIndex: Int): T {
        return playerBoard[boardIndex]
    }

    fun getValidBoardCards(): List<T> {
        return playerBoard.stream().filter { c -> c.getCardType() != CardType.INVALID }.collect(Collectors.toList())
    }

    fun moveCardRight() {
        val rightMost = getRightMostCardOnBoard()
        val leftMost = getLeftMostCardOnBoard()
        if (rightMost >= MAX_BOARD_SIZE - 1) {
            println("Cannot move cards right since rightmost indx is :$rightMost")
            return
        }
        if (leftMost <= 0) {
            println("Cannot move cards right since leftmost indx is :$leftMost")
            return
        }
        //no 0 case as this would mean the board was already full
        for (i in MAX_BOARD_SIZE - 1 downTo leftMost) {
            playerBoard[i] = playerBoard[i - 1]
        }
    }

    fun moveCardLeft() {
        val indx = getLeftMostCardOnBoard()
        if (indx <= 0) {
            println("Cannot move cards left since indx is :$indx")
            return
        }
        //no 0 case as we cant move left at this point
        for (i in 1 until MAX_BOARD_SIZE - 1) {
            playerBoard[i] = playerBoard[i + 1]
        }
    }

    fun cardIsPresentOnBoard(secondaryId: Int): Boolean {
        return playerBoard.stream().filter { c -> c.getSecondayId() == secondaryId }.count() > 0
    }

    fun cardsOnBoard(): Long {
        return playerBoard.stream().filter { c -> c.getCardType() != CardType.INVALID }.count()
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

    //temporary set to passed invalid card until fix this class use by client
    fun mergeCard(toMerge: List<DuplicatedCard>, evolve: T, toReplace: T) {
        for (card in toMerge) {
            playerBoard[card.boardIdx] = toReplace
        }
        playerBoard[toMerge[0].boardIdx] = evolve
    }
}