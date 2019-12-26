package com.game.asura.account

import java.nio.channels.SocketChannel

class AccountCache : CachedAccount {

    //player by ip+port as by ip don't work with multiple client/shared network
    private val activeAccount: MutableMap<String, PlayerAccount?> = HashMap()
    private val pendingAccount: MutableMap<String, SocketChannel> = HashMap()

    fun addPendingAccount(key: String, value: SocketChannel) {
        pendingAccount[key] = value
    }

    fun removePendingAccount(key: String) {
        pendingAccount.remove(key)
    }

    fun isValid(key: String): Boolean {
        return activeAccount.containsKey(key) || pendingAccount.containsKey(key)
    }

    fun getClientChannelInfo(key: String): ClientChannelInfo? {
        return activeAccount[key]?.clientConn
    }

    override fun addActiveAccount(key: String, value: PlayerAccount) {
        activeAccount[key] = value
        removePendingAccount(key)
    }

    fun removeActiveAccount(key: String) {
        activeAccount.remove(key)
    }

    override fun getAccount(key: String): Account? {
        return activeAccount[key]
    }
}