package com.game.asura.processor

import com.badlogic.gdx.Screen
import com.game.asura.*
import com.game.asura.card.CardType
import com.game.asura.messagein.*
import com.game.asura.parsing.DecodedMessage
import com.game.asura.screen.MatchScreen
import com.game.asura.screen.PreMatchScreen
import ktx.app.KtxGame

class MessageInProcessor(private val player: ClientPlayer,
                         private val uiManager: UIManager,
                         private val cardStore: CardStore,
                         private val parentScreen: KtxGame<Screen>,
                         private val opponent: ClientPlayer,
                         private val assetStore: AssetStore) : MessageProcessor<DecodedMessage> {

    override fun onMessage(msg: DecodedMessage) {
        when (msg) {
            is CardInfoIn -> {
                val card = cardStore.getCard(msg.secondaryCardId) ?: return
                if (card.getCardType() == CardType.MONSTER) {
                    val monster = card as MonsterDrawableCard
                    monster.update(msg.getChangedFields())
                }
            }
            is PlayerInfoIn -> {
                if (isOurMessage(msg.playerName)) {
                    player.maxMana = msg.playerMaxMana
                    player.currentMana = msg.playerCurrentMana
                    player.playerLifePoint = msg.playerHealth
                }
                if (!isOurMessage(msg.playerName)) {
                    opponent.maxMana = msg.playerMaxMana
                    opponent.currentMana = msg.playerCurrentMana
                    opponent.playerLifePoint = msg.playerHealth
                }
            }
            is CardDrawnIn -> {
                val texture = assetStore.getCardTexture(msg.primaryId) ?: return
                val card = SpellCard(texture = texture.inHandTexture, primaryId = msg.primaryId, secondaryId = msg.secondaryId,
                        cardCost = msg.cardCost, cardType = msg.cardType)

                player.handManager.addToPlayerHand(card)
                uiManager.addCardToHand(card)
                cardStore.add(card)
            }
            is MonsterCardDrawnIn -> {
                val texture = assetStore.getCardTexture(msg.primaryId) ?: return
                val card = MinionCard(texture = texture.inHandTexture, primaryId = msg.primaryId, secondaryId = msg.secondaryId,
                        cardCost = msg.cardCost, cardType = msg.cardType, attack = msg.attack,
                        health = msg.health, maxHealth = msg.maxHealth, owner = player.playerName)
                player.handManager.addToPlayerHand(card)
                uiManager.addCardToHand(card)
                cardStore.add(card)
            }
            is MonsterCardPlayedIn -> {
                if (!isOurMessage(msg.accountName)) {
                    val texture = assetStore.getCardTexture(msg.primaryId) ?: return
                    //opponent played card
                    val card = MinionCard(texture = texture.onBoardTexture, primaryId = msg.primaryId, secondaryId = msg.secondaryId,
                            cardCost = msg.cardCost, cardType = CardType.MONSTER, attack = msg.attack,
                            health = msg.health, maxHealth = msg.maxHealth, owner = opponent.playerName)
                    opponent.boardManager.updatePlayerBoard(card, msg.boardIndx)
                    uiManager.addEnemyMonsterToBoard(card, msg.boardIndx)
                    cardStore.add(card)
                    return
                }
                val card = player.handManager.getCardFromHand(msg.secondaryId) ?: return
                if (card !is MinionCard) {
                    println("Error, card $card should be a minion.")
                    return
                }
                println("Removing card:$card from player hand.")
                player.handManager.removeFromHand(card)
                // put it in play
                player.boardManager.updatePlayerBoard(card, msg.boardIndx)
                uiManager.addMonsterToBoard(card, msg.boardIndx)
                cardStore.add(card)

            }
            is StartTurnIn -> {
                uiManager.startTurnTimer()
                player.myTurn = true
                player.currentPhase = msg.phase
            }
            is EndTurnIn -> {
                println("End Turn Received from server.")
                player.myTurn = false
                //cancel all pending action etc here
            }
            is PhaseChangeIn -> {
                player.currentPhase = msg.nextPhase
            }
            is MatchEndIn -> {
                println("Match result:${msg.matchResult}")
                parentScreen.removeScreen<MatchScreen>()
                parentScreen.setScreen<PreMatchScreen>()
            }
            is MonsterEvolveIn -> {
                val texture = assetStore.getCardTexture(msg.primaryCardId) ?: return
                val evolved = MinionCard(texture.inHandTexture, msg.primaryCardId, msg.secondaryCardId, msg.cardCost, msg.cardType, msg.attack, msg.health, msg.maxHealth, player.playerName)
                val firstMonster = cardStore.getCard(msg.firstMonsterId) ?: return
                val secondMonster = cardStore.getCard(msg.secondMonsterId) ?: return
                if (firstMonster !is MinionCard || secondMonster !is MinionCard) {
                    println("Error, card $firstMonster and card $secondMonster should be minions.")
                    return
                }
                firstMonster.actor.remove()
                secondMonster.actor.remove()
                if (!isOurMessage(msg.accountName)) {
                    uiManager.addEnemyMonsterToBoard(evolved, msg.boardPosition)
                    opponent.boardManager.removeCard(firstMonster)
                    opponent.boardManager.removeCard(secondMonster)
                    opponent.boardManager.updatePlayerBoard(evolved, msg.boardPosition)
                    return
                }
                player.boardManager.removeCard(firstMonster)
                player.boardManager.removeCard(secondMonster)
                uiManager.addMonsterToBoard(evolved, msg.boardPosition)
                player.boardManager.updatePlayerBoard(evolved, msg.boardPosition)
            }
            is CardPlayedIn -> {
                if (isOurMessage(msg.accountName)) {
                    val card = player.handManager.getCardFromHand(msg.secondaryId) ?: return
                    println("Removing card:$card from player hand.")
                    player.handManager.removeFromHand(card)
                    uiManager.removeCardfromHand(card)
                }
            }
            is MonsterDeathIn -> {
                val card = cardStore.getCard(msg.secondaryId) ?: return
                if (card !is MinionCard) {
                    println("Error card:$card is not a monster but MonsterDeath was received.")
                    return
                }
                println("Monster $card died, removing from battlefield.")
                card.actor.remove()
                if (card.getOwner() == player.playerName) {
                    player.boardManager.removeCard(card)
                } else {
                    opponent.boardManager.removeCard(card)
                }
            }
            else -> {
                println("Unable to process message:$msg missing logic.")
            }
        }
    }

    private fun isOurMessage(accountName: String): Boolean {
        return accountName.toLowerCase() == player.playerName.toLowerCase()
    }
}