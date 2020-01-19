package com.game.asura

import com.badlogic.gdx.graphics.Texture
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import java.io.File

class AssetStore {
    private val NULL_TEXTURE = Texture("core/assets/null_texture.png")
    private val textures: Map<Asset, Texture> = Asset.values().map { it to Texture(it.path) }.toMap()
    private val cardTexture: Map<Int, CardTexture>
    init {
        val file = File("core/assets/cardtexture.json")
        val input = file.inputStream()
        val inputStr = input.bufferedReader().use {
            it.readText()
        }
        val json = Json(JsonConfiguration.Stable)
        val cardTextures: List<CardTexture> = json.parse(CardTexture.serializer().list, inputStr)
        cardTexture = cardTextures.map { it.id to it }.toMap()
    }

    fun getTexture(name: Asset): Texture {
        return textures[name] ?: NULL_TEXTURE

    }

    fun getCardTexture(primaryId: Int): CardTexture? {
        return cardTexture[primaryId]
    }
}