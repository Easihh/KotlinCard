package com.game.asura.account

import com.game.asura.parsing.Tokenizer
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class ClientChannelInfo(private val client: SocketChannel,
                        private val readBuffer: ByteBuffer) {

    val tokenizer: Tokenizer = Tokenizer(readBuffer)

    fun readIntoBuffer(): Int {
        val bytesRead = client.read(readBuffer)
        tokenizer.flip()
        return bytesRead
    }
}