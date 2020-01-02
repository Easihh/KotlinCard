package com.game.asura

class BoardManager<T : Card>(create: () -> T) {

    private val playerBoard: MutableList<T> = ArrayList()

    init {
        for (x in 0 until MAX_BOARD_SIZE) {
            playerBoard.add(create())
        }
    }

    fun boardIsEmpty(): Boolean {
        var count = 0
        for (index in 0..6) {
            if (playerBoard[index].getCardType() != CardType.INVALID) {
                count++
            }
        }
        return count == 0
    }

    fun getRightMostCardOnBoard(): Int {
        for (indx in 6 downTo 0) {
            if (playerBoard[indx].getCardType() != CardType.INVALID) {
                return indx
            }
        }
        //no board, return the mid position
        return MAX_BOARD_SIZE / 2
    }

    fun getLeftMostCardOnBoard(): Int {
        for (indx in 0 until 7) {
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

    fun getBoardIndexBySecondaryId(secondaryCardId: Int): Int? {
        for (i in 0..6) {
            if (playerBoard[i].getSecondayId() == secondaryCardId) {
                return i
            }
        }
        return null
    }

    fun getCardByBoardIndex(boardIndex: Int): T {
        return playerBoard[boardIndex]
    }
}