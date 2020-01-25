package com.game.asura.messagein

import com.game.asura.parsing.DecodedMessage

data class PlayerInfoIn(val playerName: String,
                        val playerCurrentMana: Int,
                        val playerMaxMana: Int,
                        val playerHealth: Int) : DecodedMessage {
}