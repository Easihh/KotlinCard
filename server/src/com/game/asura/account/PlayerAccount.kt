package com.game.asura.account

import com.game.asura.Match
import com.game.asura.ServerPlayer
import com.game.asura.parsing.Tokenizer

class PlayerAccount(val clientConn: ClientChannelInfo,
                    private val clientWriter: ChannelMessageWriter,
                    private val accountKey: String,
                    private val accountName: String,
                    private var currentMatch: Match? = null) : Account {

    override fun getPlayer(): ServerPlayer? {
        return currentMatch?.getPlayer(accountName)
    }

    override fun setMatch(match: Match) {
        this.currentMatch = match
    }

    override fun getCurrentMatchId(): Int? {
        return this.currentMatch?.matchId
    }

    override fun getChannelWriter(): ChannelMessageWriter {
        return clientWriter
    }

    override fun getTokenizer(): Tokenizer {
        return clientConn.tokenizer
    }

    override fun getAccountKey(): String {
        return accountKey
    }

    override fun getAccountName(): String {
        return accountName
    }
}