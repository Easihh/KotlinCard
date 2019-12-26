package com.game.asura

import com.game.asura.messageout.LoginRequestOut
import com.game.asura.messageout.OutMessage
import com.game.asura.processor.MessageDecoder
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class ServerBosom(private val insertableQueue: InsertableQueue) {

    private val SERVER_HOST: String = "localhost"
    private val SERVER_PORT: Int = 8555

    private inner class ServerMessageReader : Runnable {
        private val messageDecoder = MessageDecoder(insertableQueue)
        private var tokenizer = Tokenizer(bufferRead)
        override fun run() {
            val byteArr = ByteArray(1460)
            var isConnected = true
            while (isConnected) {
                val byteRead = socketInput.read(byteArr)
                if (byteRead == 0) {
                    println("ERROR:Read 0 bytes")
                    return
                }
                if (byteRead == -1) {
                    println("Received end of stream, client is disconnected from server.")
                    isConnected = false
                    return
                }
                val tmpArr = byteArr.copyOfRange(0, byteRead)
                bufferRead = ByteBuffer.wrap(tmpArr)
                bufferRead.position(byteRead)
                //req until move to channel on client since we are recreating buffer here
                tokenizer = Tokenizer(bufferRead)
                messageDecoder.decode(tokenizer)

                println("Thread:" + Thread.currentThread().name)
            }
        }
    }

    private lateinit var socketInput: InputStream
    private lateinit var socketOutput: OutputStream
    private var bufferRead = ByteBuffer.allocate(1460)
    private var bufferWrite = ByteBuffer.allocate(1460)
    private val messageBuilder = MessageBuilder(bufferWrite)

    fun connect(): Boolean {
        println("Requesting connection to server.")
        try {
            val socket = Socket(SERVER_HOST, SERVER_PORT)
            socketInput = socket.getInputStream()
            socketOutput = socket.getOutputStream()
            Executors.newSingleThreadExecutor()
            thread(true, true, null, "MessageReader") {
                ServerMessageReader().run()
            }
            val connRequest = LoginRequestOut("Asura")
            insertableQueue.addMessage(connRequest)
        } catch (e: Exception) {
            println("Error Connecting to server:$SERVER_HOST with port:$SERVER_PORT.")
            return false
        }
        println("Connection Success.")
        return true

    }

    fun sendMessage(msg: OutMessage) {
        msg.build(messageBuilder)
        val byteArr = ByteArray(bufferWrite.limit())
        bufferWrite.get(byteArr, 0, byteArr.size)
        println("Message sent:${messageBuilder.printMessage()}")
        socketOutput.write(byteArr)
        bufferWrite.clear()
    }
}