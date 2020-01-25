package com.game.asura.messagein

import com.game.asura.account.Account
import com.game.asura.parsing.DecodedMessage

data class LoginRequestIn(val playerAccount: Account) : DecodedMessage