package com.game.asura

import com.badlogic.gdx.graphics.Texture
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

val NULL_CARDTEXTURE = Texture("core/assets/null_texture.png")
val NULL_BOARD_CARDTEXTURE = Texture("core/assets/empty_board_card.png")

@Serializable
data class CardTexture(val id: Int,
                       private val inHand: String? = null,
                       private val onBoard: String? = null) {

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
}