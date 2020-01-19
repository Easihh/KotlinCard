package com.game.asura

import com.badlogic.gdx.graphics.Texture
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class CardTexture(val id: Int,
                       val inHand: String? = null,
                       val onBoard: String? = null,
                       val other: String? = null) {

    @Transient
    private val NULL_CARDTEXTURE = Texture("core/assets/null_texture.png")
    @Transient
    val inHandTexture: Texture = if (inHand != null) {
        Texture(inHand)
    } else {
        NULL_CARDTEXTURE
    }
    @Transient
    val onBoardTexture: Texture = if (onBoard != null) {
        Texture(onBoard)
    } else {
        NULL_CARDTEXTURE
    }
    @Transient
    val otherTexture: Texture = if (other != null) {
        Texture(other)
    } else {
        NULL_CARDTEXTURE
    }
}