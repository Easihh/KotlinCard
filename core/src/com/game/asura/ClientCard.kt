package com.game.asura

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import kotlin.random.Random

val INVALID_CLIENT_CARD = ClientCard(-1, -1, 99, CardType.INVALID)

class ClientCard(private val primaryId: Int,
                 private val secondaryId: Int = Random.nextInt(),
                 private var cardCost: Int,
                 private val cardType: CardType) : DrawableCard {


    private var cardActor: CardActor

    init {
        val picture = if (cardType == CardType.MONSTER) {
            "monsterCard.png"
        } else "card.png"
        val texture = Texture("core/assets/$picture")
        cardActor = CardActor(texture, secondaryId)
    }

    override fun getActor(): Actor {
        return cardActor
    }

    override fun transformActor(texture: Texture) {
        cardActor.drawable = TextureRegionDrawable(TextureRegion(texture))
        cardActor.setSize(cardActor.prefWidth, cardActor.prefHeight)
        cardActor.setScaling(Scaling.stretch)
        cardActor.setAlign(Align.center)
        cardActor.setScale(1.0f)
    }

    override fun getCost(): Int {
        return cardCost
    }

    override fun getPrimaryId(): Int {
        return primaryId
    }

    override fun getSecondayId(): Int {
        return secondaryId
    }

    override fun getCardType(): CardType {
        return cardType
    }

    override fun getEffect(): List<CardEffect> {
        return ArrayList()
    }

    override fun toString(): String {
        return "cardType=$cardType,cardPrimaryId=$primaryId"
    }

    override fun getAttack(): Int? {
        return 1
    }

    override fun getHealth(): Int? {
        return 3
    }


}