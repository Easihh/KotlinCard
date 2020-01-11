package com.game.asura

class MatchFinder {

    private val allMatch: MutableMap<Int, Match> = HashMap()


    fun findMatch(matchId: Int?): Match? {
        return allMatch[matchId]
    }

    fun addMatch(match: Match) {
        allMatch[match.matchId] = match
    }

}