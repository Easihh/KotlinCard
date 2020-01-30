package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.game.asura.card.BaseCard
import com.game.asura.card.CardType
import kotlin.random.Random

abstract class DrawableCard(primaryId: Int,
                            secondaryId: Int = Random.nextInt(),
                            cardCost: Int,
                            cardType: CardType,
                            ability: List<Int> = ArrayList()) : BaseCard(primaryId, secondaryId, cardCost, cardType, ability) {

    lateinit var actor: Actor

    abstract fun transformActor(texture: Texture)

    abstract fun initCardTexture(texture: Texture)

}