package com.game.asura

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
        return CardPlayedData(coreMsg.getCardPrimaryId(), coreMsg.getSecondaryCardId(),
                coreMsg.getMatchId(), coreMsg.getCardTarget(),coreMsg.getBoardPosition())
    }

    fun getGameRequestData(): GameRequestData {
        return GameRequestData(coreMsg.getGameType())
    }

    fun getPlayerInfoData(): PlayerInfoData {
        return PlayerInfoData(coreMsg.getAccountName(), coreMsg.getCurrentPlayerLife(), coreMsg.getMaxPlayerLife(),
                coreMsg.getCurrentPlayerMana(), coreMsg.getMaxPlayerMana())
    }

    fun getMatchInfoData(): MatchInfoData {
        return MatchInfoData(coreMsg.getMatchId(), coreMsg.getGameType())
    }

    fun getCardDrawnData(): CardDrawnData {
        return CardDrawnData(coreMsg.getPrimaryCardId(), coreMsg.getSecondaryCardId(), coreMsg.getCardCost(),
                coreMsg.getCardType())
    }


}