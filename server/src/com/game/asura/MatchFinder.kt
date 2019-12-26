package com.game.asura

class MatchFinder {

    private val allMatch: MutableMap<Int, Match<ServerPlayer>> = HashMap()


    fun findMatch(matchId: Int): Match<ServerPlayer>? {
        return allMatch[matchId]
    }

    fun addMatch(match: Match<ServerPlayer>) {
        allMatch[match.matchId] = match
    }

}