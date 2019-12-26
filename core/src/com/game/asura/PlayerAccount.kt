package com.game.asura

class PlayerAccount(val player: ClientPlayer) {

    private var currentMatch: Match<ClientPlayer>? = null

    fun setMatch(match: Match<ClientPlayer>) {
        currentMatch = match
    }

    fun getCurrentMatch(): Match<ClientPlayer>? {
        return currentMatch
    }
}