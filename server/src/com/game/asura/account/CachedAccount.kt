package com.game.asura.account

interface CachedAccount {

    fun addActiveAccount(key: String, value: PlayerAccount)
    fun getAccount(key: String): Account?
}