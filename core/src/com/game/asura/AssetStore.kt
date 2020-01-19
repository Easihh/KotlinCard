package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.game.asura.card.CardInfoStore
import com.game.asura.card.JsonCardList

class AssetStore(private val cardInfoStore: CardInfoStore) {
    private val NULL_TEXTURE = Texture("core/assets/null_texture.png")
    private val textures: Map<Asset, Texture> = Asset.values().map { it to Texture(it.path) }.toMap()
    private val cardTexture: Map<Int, Texture> = CardAsset.values().map { it.primaryId to Texture(it.path) }.toMap()
    private val boardCardTexture: Map<Int, Texture> = MonsterCardAsset.values().map { it.primaryId to Texture(it.path) }.toMap()


    fun getTexture(name: Asset): Texture {
        return textures[name] ?: NULL_TEXTURE

    }

    fun getCardTexture(primaryId: Int): Texture {
        return cardTexture[primaryId] ?: NULL_TEXTURE
    }

    fun getBoardCardTexture(primaryId: Int): Texture {
        return boardCardTexture[primaryId] ?: NULL_TEXTURE
    }
}