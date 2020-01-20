package com.game.asura.processor

import com.game.asura.LoginStatus
import com.game.asura.messagein.LoginReplyIn
import com.game.asura.parsing.DecodedMessage

class LoginMessageInProcessor(val toPreMatchScreen: () -> Unit) : MessageProcessor<DecodedMessage> {

    override fun onMessage(msg: DecodedMessage) {
        when (msg) {
            is LoginReplyIn -> {
                if (msg.loginStatus == LoginStatus.CONNECTED) {
                    println("Login Success.")
                    toPreMatchScreen()
                } else {
                    println("Login Failed.")
                }
            }
            else -> {
                println("Unable to process message:$msg missing logic.")
            }
        }
    }
}