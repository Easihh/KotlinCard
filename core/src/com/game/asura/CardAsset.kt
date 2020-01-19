package com.game.asura

enum class CardAsset(val primaryId: Int,
                     val path: String) {
    UNKNOWN(0, "core/assets/null_texture.png"),
    FIRST_MONSTER(1, "core/assets/slime.png"),
    FIRST_TARGET_SPELL(2, "core/assets/card.png"),
    //FIRST_SPELL(3, ""),
    //KING_SLIME(4, "")
    MAGE_HERO(5, "core/assets/hero.png")
}