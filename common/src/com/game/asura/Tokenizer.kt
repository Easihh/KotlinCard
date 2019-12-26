package com.game.asura

import java.lang.StringBuilder
import java.nio.ByteBuffer

class Tokenizer(private val buffer: ByteBuffer) {

    private val enumMapper = EnumMapper()
    private var currentField: Int? = null
    private var currentType: Char? = null

    fun flip() {
        buffer.flip()
    }

    fun rewind() {
        buffer.rewind()
    }

    fun clear() {
        buffer.clear()
    }

    fun hasField(field: MessageField): Boolean {
        val pos = buffer.position()
        while (hasRemaining()) {
            val nField = MessageField.getMessageField(nextField())
            nextType()
            if (field == nField) {
                return true
            }
            nextValue()
        }
        buffer.position(pos)
        return false
    }

    fun hasRemaining(): Boolean {
        return buffer.hasRemaining()
    }

    fun nextField(): Int {
        currentField = null
        currentType = null
        return buffer.int
    }

    fun nextType(): Char {
        val nextType = buffer.get().toChar()
        currentType = nextType
        return nextType
    }

    fun nextValue(): Any? {
        when (currentType) {
            'B' -> {
                return buffer.get()
            }
            'I' -> {
                return buffer.int
            }
            'L' -> {
                return buffer.long
            }
            'S' -> {
                val length = buffer.get().toInt()
                val byteArr = ByteArray(length)
                buffer.get(byteArr, 0, length)
                return String(byteArr)
            }
        }
        println("Error next value is null, currentType=$currentType.")
        return null
    }

    //don't want to overwrite toString since its called when using "this" in method such as buffer clear
    fun printMessage(): String {
        val pos = buffer.position()
        buffer.position(0)
        val sb = StringBuilder()
        while (hasRemaining()) {
            val field = nextField()
            val fieldName = MessageField.getMessageField(field)
            val type = nextType()
            val fieldValue: Any?
            when (type) {
                'B' -> {
                    fieldValue = nextValue() as Byte
                    val fieldValueStr = enumMapper.getFieldValueName(fieldName, fieldValue)
                    sb.append(fieldName).append("[$field$type] = $fieldValueStr , ")
                }
                'I' -> {
                    fieldValue = nextValue() as Int
                    val fieldValueName = enumMapper.getFieldValueName(fieldName, fieldValue)
                    val fieldValueStr = if (fieldValueName != null) {
                        "$fieldValue($fieldValueName)"
                    } else fieldValue
                    sb.append(fieldName).append("[$field$type] = $fieldValueStr , ")
                }
                'L' -> {
                    fieldValue = nextValue() as Long
                    sb.append(fieldName).append("[$field$type] = $fieldValue , ")
                }
                'S' -> {
                    fieldValue = nextValue() as String
                    sb.append(fieldName).append("[$field$type] = $fieldValue , ")
                }
                else -> {
                    println("Invalid Field Type of $type, ignoring field $fieldName.")
                }
            }
        }
        buffer.position(pos)
        return sb.toString()
    }
}