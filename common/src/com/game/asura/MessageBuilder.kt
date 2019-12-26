package com.game.asura

import java.nio.ByteBuffer

class MessageBuilder(private val buffer: ByteBuffer) {

    fun add(field: MessageField, value: Any) {
        buffer.putInt(field.fieldNumber)
        buffer.put(field.type)
        when (field.type.toChar()) {
            'B' -> {
                buffer.put(value as Byte)
            }
            'I' -> {
                buffer.putInt(value as Int)
            }
            'L' -> {
                buffer.putLong(value as Long)
            }
            'S' -> {
                val strVal = value as String
                buffer.put(strVal.length.toByte())
                buffer.put(strVal.toByteArray())
            }
            else -> {
                println("Unable to add field:$field with value:$value to the message builder.")
            }
        }
    }

    fun flip() {
        buffer.flip()
    }

    fun clear(){
        buffer.clear()
    }

    //don't want to overwrite toString since its called when using "this" in method such as buffer clear
    fun printMessage(): String {
        val tokenizer = Tokenizer(buffer)
        return tokenizer.printMessage()
    }
}