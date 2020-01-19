package com.game.asura

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import java.io.File

class JsonCardInfo {

    private val cards: List<CardInfo>

    init {
        val file = File("core/assets/cardinfo.json")
        val input = file.inputStream()
        val inputStr = input.bufferedReader().use {
            it.readText()
        }
        val json = Json(JsonConfiguration.Stable)
        cards = json.parse(CardInfo.serializer().list, inputStr)
    }

    fun getAllCardInfo(): List<CardInfo> {
        return cards
    }
}