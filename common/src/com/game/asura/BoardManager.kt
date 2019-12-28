package com.game.asura

//to instantiate the generic array
//inline fun <reified T> BoardManager() = BoardManager(arrayOfNulls(MAX_BOARD_SIZE))
//@PublishedApi internal constructor(private val playerBoard: Array<T?>)
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

    fun getPlayerBoard(): MutableList<T> {
        return playerBoard
    }
}