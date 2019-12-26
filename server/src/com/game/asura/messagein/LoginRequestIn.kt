package com.game.asura.messagein

import com.game.asura.account.Account
import com.game.asura.DecodedMessage

class LoginRequestIn(private val playerAccount: Account) : DecodedMessage {

    fun getPlayerAccount(): Account {
        return playerAccount
    }
}