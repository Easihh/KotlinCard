package com.game.asura

import com.game.asura.card.Card
import com.game.asura.card.CardType

class BoardManager<T : Card>(create: () -> T) {

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
            //println("Card At i=${playerBoard[i]}")
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
        /*for (i in MAX_BOARD_SIZE - 1 downTo indx) {
            println("Card At i=${playerBoard[i - 1]}")
            playerBoard[i - 1] = playerBoard[i]
        }*/
    }

    fun cardIsPresentOnBoard(secondaryId: Int): Boolean {
        return playerBoard.stream().filter { c -> c.getSecondayId() == secondaryId }.count() > 0
    }

    fun cardsOnBoard(): Long {
        return playerBoard.stream().filter { c -> c.getCardType() != CardType.INVALID }.count()
    }

    fun removeCard(target: T) {
        playerBoard.removeIf { t -> t.getSecondayId() == target.getSecondayId() }
    }
}