package com.game.asura.processor

import com.game.asura.InsertableQueue
import com.game.asura.card.CardType
import com.game.asura.messagein.*
import com.game.asura.messaging.MessageType
import com.game.asura.parsing.CoreMessageParser
import com.game.asura.parsing.DecodedMessage
import com.game.asura.parsing.Tokenizer

class MessageDecoder(private val queue: InsertableQueue) : CoreMessageParser() {

    fun decode(tokenizer: Tokenizer) {
        //pos of 0 due to last compact wont copy anything and after flip nothing remains
        while (tokenizer.hasRemaining() && tokenizer.position() > 0) {
            doDecode(tokenizer)
        }
        tokenizer.clear()
    }

    private fun doDecode(tokenizer: Tokenizer) {
        tokenizer.flip()
        parseMessage(tokenizer)
        var decodedMessage: DecodedMessage? = null
        when (val msgType = getMessageType()) {
            MessageType.LOGIN_REPLY -> {
                val data = getLoginReplyData()
                val status = data.logStatus ?: return
                decodedMessage = LoginReplyIn(status)
            }
            MessageType.PLAYER_INFO -> {
                val data = getPlayerInfoData()
                val mana = data.currentMana ?: return
                val maxMana = data.maxMana ?: return
                val name = data.playerName ?: return
                val health = data.health ?: return
                decodedMessage = PlayerInfoIn(name, mana, maxMana, health)
            }
            MessageType.CARD_DRAWN -> {
                val data = getCardDrawnData()
                val cardCost = data.cardCost ?: return
                val primaryId = data.primaryId ?: return
                val secondaryId = data.secondaryId ?: return
                val cardType = data.cardType ?: return
                decodedMessage = if (cardType == CardType.MONSTER) {
                    val health = data.health ?: return
                    val maxHealth = data.maxHealth ?: return
                    val attack = data.attack ?: return
                    MonsterCardDrawnIn(primaryId, secondaryId, cardCost, cardType, attack, health, maxHealth)
                } else {
                    CardDrawnIn(primaryId, secondaryId, cardCost, cardType)
                }
            }
            MessageType.MATCH_START -> {
                val data = getMatchStartData()
                val accountName = data.accountName ?: return
                val enemyName = data.enemyAccountName ?: return
                decodedMessage = MatchStartIn(accountName, enemyName)
            }
            MessageType.MATCH_END -> {
                val data = getMatchEndData()
                val accountName = data.accountName ?: return
                val matchResult = data.matchResult ?: return
                decodedMessage = MatchEndIn(accountName, matchResult)
            }
            MessageType.CARD_INFO -> {
                val data = getCardInfoData()
                val playerName = data.playerName ?: return
                val primaryId = data.primaryCardId ?: return
                val secondary = data.secondaryCardId ?: return
                decodedMessage = CardInfoIn(playerName, primaryId, secondary, data.playerHealth, data.playerMaxHealth, data.summonIllness)
            }
            MessageType.MONSTER_CARD_PLAYED -> {
                val data = getMonsterCardPlayedData()
                val primaryId = data.cardPrimaryId ?: return
                val secondary = data.cardSecondaryId ?: return
                val accountName = data.accountName ?: return
                val attack = data.attack ?: return
                val health = data.health ?: return
                val maxHealth = data.maxHealth ?: return
                val boardIndx = data.boardIndx ?: return
                val cardCost = data.cardCost ?: return
                decodedMessage = MonsterCardPlayedIn(primaryId, secondary, accountName, attack, health, maxHealth, boardIndx, cardCost)
            }
            MessageType.CARD_PLAYED -> {
                val data = getCardPlayedData()
                val primaryId = data.cardPrimaryId ?: return
                val secondary = data.cardSecondaryId ?: return
                val accountName = data.accountName ?: return
                decodedMessage = CardPlayedIn(primaryId, secondary, accountName)
            }
            MessageType.START_TURN -> {
                val data = getStartTurnData()
                val phase = data.nextPhase ?: return
                decodedMessage = StartTurnIn(phase)
            }
            MessageType.END_TURN -> {
                val data = getEndTurnData()
                decodedMessage = EndTurnIn()
            }
            MessageType.MONSTER_EVOLVE -> {
                val data = getMonsterEvolveData()
                val boardIndx = data.boardPosition ?: return
                val firstMonsterId = data.firstMonsterId ?: return
                val secondMonsterId = data.secondMonsterId ?: return
                val cardType = data.cardType ?: return
                val cardCost = data.cardCost ?: return
                val primaryId = data.primaryCardId ?: return
                val secondaryId = data.secondaryCardId ?: return
                val attack = data.attack ?: return
                val health = data.health ?: return
                val maxHealth = data.maxHealth ?: return
                val accountName = data.accountName ?: return
                decodedMessage = MonsterEvolveIn(primaryId, secondaryId, firstMonsterId, secondMonsterId,
                        boardIndx, cardCost, cardType, attack, health, maxHealth, accountName)
            }
            MessageType.PHASE_CHANGE -> {
                val data = getPhaseEndData()
                val nextPhase = data.nextPhase ?: return
                decodedMessage = PhaseChangeIn(nextPhase)
            }
            MessageType.MONSTER_DEATH -> {
                val data = getMonsterDeathData()
                val primaryId = data.primaryId ?: return
                val secondaryId = data.secondaryId ?: return
                decodedMessage = MonsterDeathIn(primaryId, secondaryId)
            }
            else -> {
                println("Message of type $msgType has no decode logic.")
            }
        }
        if (decodedMessage == null) {
            println("Cannot process Decoded Message since it is null.")
            return
        }
        queue.addMessage(decodedMessage)
        tokenizer.compact()
    }
}