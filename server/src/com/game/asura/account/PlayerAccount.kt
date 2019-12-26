package com.game.asura.account

import com.game.asura.Tokenizer

class PlayerAccount(val clientConn: ClientChannelInfo,
                    private val clientWriter: ChannelMessageWriter,
                    private val accountKey: String,
                    private val accountName: String) : Account {

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