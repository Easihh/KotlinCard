package com.game.asura.account

import com.game.asura.Tokenizer

interface Account {
    fun getTokenizer(): Tokenizer
    fun getAccountKey(): String
    fun getAccountName(): String
    fun getChannelWriter(): ChannelMessageWriter
}