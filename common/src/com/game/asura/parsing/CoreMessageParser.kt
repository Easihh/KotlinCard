package com.game.asura.parsing

import com.game.asura.END_MESSAGE_DELIMITER
import com.game.asura.EnumMapper
import com.game.asura.message.data.*
import com.game.asura.messaging.CoreMessage
import com.game.asura.messaging.MessageField
import com.game.asura.messaging.MessageType

abstract class CoreMessageParser {

    private val coreMsg = CoreMessage()
    private val enumMapper = EnumMapper()

    fun parseMessage(tokenizer: Tokenizer) {
        coreMsg.clear()
        val sb = StringBuilder()
        while (tokenizer.hasRemaining()) {
            val field = tokenizer.nextField()
            val fieldName = MessageField.getMessageField(field)
            val type = tokenizer.nextType()
            var fieldValue: Any?
            when (type) {
                'B' -> {
                    fieldValue = tokenizer.nextValue() as Byte
                    val fieldValueStr = enumMapper.getFieldValueName(fieldName, fieldValue)
                    sb.append(fieldName).append("[$field$type] = $fieldValueStr , ")
                }
                'I' -> {
                    fieldValue = tokenizer.nextValue() as Int
                    val fieldValueName = enumMapper.getFieldValueName(fieldName, fieldValue)
                    val fieldValueStr = if (fieldValueName != null) {
                        "$fieldValue($fieldValueName)"
                    } else fieldValue
                    sb.append(fieldName).append("[$field$type] = $fieldValueStr , ")
                }
                'L' -> {
                    fieldValue = tokenizer.nextValue() as Long
                    sb.append(fieldName).append("[$field$type] = $fieldValue , ")
                }
                'C' -> {
                    fieldValue = tokenizer.nextValue() as Char
                    sb.append(fieldName).append("[$field$type] = $fieldValue , ")
                }
                'S' -> {

                    fieldValue = tokenizer.nextValue() as String
                    sb.append(fieldName).append("[$field$type] = $fieldValue , ")
                }
                else -> {
                    println("Invalid Field Type of $type, ignoring field $fieldName.")
                    println("Parsed msg:$sb")
                    return
                }
            }
            coreMsg.addField(fieldName, type, fieldValue)
            if (fieldName == MessageField.END_MESSAGE && fieldValue == END_MESSAGE_DELIMITER) {
                println("Found end of message delimiter; won't parse further.")
                break
            }
        }
        println("MessageRecv:$sb")
    }

    fun getMessageType(): MessageType {
        return coreMsg.getMessageType()
    }

    fun getCardPlayedData(): CardPlayedData {
        return CardPlayedData(coreMsg.getPrimaryCardId(), coreMsg.getSecondaryCardId()
                , coreMsg.getCardTarget(), coreMsg.getAccountName(), coreMsg.getCardType(), coreMsg.getBoardPosition())
    }

    fun getMonsterCardPlayedData(): MonsterCardPlayedData {
        return MonsterCardPlayedData(coreMsg.getPrimaryCardId(), coreMsg.getSecondaryCardId(), coreMsg.getAccountName()
                , coreMsg.getCardAttack(), coreMsg.getCardHealth(), coreMsg.getMaxCardLife(),
                coreMsg.getBoardPosition(), coreMsg.getCardCost())
    }

    fun getGameRequestData(): GameRequestData {
        return GameRequestData(coreMsg.getGameType())
    }

    fun getCardInfoData(): CardInfoData {
        return CardInfoData(coreMsg.getAccountName(), coreMsg.getPrimaryCardId(), coreMsg.getSecondaryCardId(),
                coreMsg.getCardHealth(), coreMsg.getMaxCardLife(),coreMsg.getSummonIllness())
    }

    fun getMatchStartData(): MatchStartData {
        return MatchStartData(coreMsg.getAccountName(), coreMsg.getEnemyAccountName())
    }

    fun getMatchEndData(): MatchEndData {
        return MatchEndData(coreMsg.getAccountName(), coreMsg.getMatchResult())
    }

    fun getCardDrawnData(): CardDrawnData {
        return CardDrawnData(coreMsg.getPrimaryCardId(), coreMsg.getSecondaryCardId(), coreMsg.getCardCost(),
                coreMsg.getCardType(), coreMsg.getCardAttack(), coreMsg.getCardHealth(), coreMsg.getMaxCardLife())
    }

    fun getStartTurnData(): StartTurnData {
        return StartTurnData(coreMsg.getNextPhase())
    }

    fun getEndTurnData(): EndTurnData {
        return EndTurnData()
    }

    fun getMonsterAttackData(): MonsterAttackData {
        return MonsterAttackData(coreMsg.getPrimaryCardId(), coreMsg.getSecondaryCardId(), coreMsg.getCardTarget())
    }

    fun getPlayerInfoData(): PlayerInfoData {
        return PlayerInfoData(coreMsg.getAccountName(), coreMsg.getCurrentPlayerMana(),
                coreMsg.getMaxPlayerMana(), coreMsg.getPlayerHealth())
    }

    fun getLoginReplyData(): LoginReplyData {
        return LoginReplyData(coreMsg.getLoginStatus())
    }

    fun getMonsterEvolveData(): MonsterEvolveData {
        return MonsterEvolveData(coreMsg.getPrimaryCardId(), coreMsg.getSecondaryCardId(), coreMsg.getCardCost(), coreMsg.getCardType(),
                coreMsg.getFirstMonsterId(), coreMsg.getSecondMonsterId(), coreMsg.getBoardPosition(),
                coreMsg.getCardAttack(), coreMsg.getCardHealth(), coreMsg.getMaxCardLife(), coreMsg.getAccountName())
    }

    fun getPhaseEndData(): PhaseChangeData {
        return PhaseChangeData(coreMsg.getNextPhase())
    }
}