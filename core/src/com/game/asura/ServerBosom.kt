package com.game.asura

import com.game.asura.messageout.LoginRequestOut
import com.game.asura.messageout.OutMessage
import com.game.asura.messaging.MessageBuilder
import com.game.asura.parsing.Tokenizer
import com.game.asura.processor.MessageDecoder
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import kotlin.concurrent.thread

class ServerBosom(private val insertableQueue: InsertableQueue) {

    private val SERVER_HOST: String = "localhost"
    private val SERVER_PORT: Int = 8555

    private inner class ServerMessageReader(private val accountName: String) : Runnable {
        private val messageDecoder = MessageDecoder(insertableQueue)
        private val tokenizer = Tokenizer(bufferRead)
        var isConnected = true

        override fun run() {
            channel = SocketChannel.open(InetSocketAddress(SERVER_HOST, SERVER_PORT))
            channel.configureBlocking(false)
            val selector = Selector.open()
            channel.register(selector, SelectionKey.OP_READ)

            //Login
            println("Requesting login to server with account $accountName")
            val connRequest = LoginRequestOut(accountName)
            insertableQueue.addMessage(connRequest)

            while (isConnected) {
                selector.select()
                val selectedKeys = selector.selectedKeys()
                val itr = selectedKeys.iterator()
                while (itr.hasNext()) {
                    val key = itr.next()
                    if (key.isReadable) {
                        onMessageReceived(key)
                    }
                    itr.remove()
                }
            }
        }

        private fun onMessageReceived(selectedKey: SelectionKey) {
            val channel = selectedKey.channel() as SocketChannel
            val byteRead = channel.read(bufferRead)
            if (byteRead == 0) {
                println("ERROR:Read 0 bytes")
                return
            }
            if (byteRead == -1) {
                println("Received end of stream, client is disconnected from server.")
                isConnected = false
                return
            }
            messageDecoder.decode(tokenizer)
        }
    }


    private var bufferRead = ByteBuffer.allocate(1460)
    private var bufferWrite = ByteBuffer.allocate(1460)
    private val messageBuilder = MessageBuilder(bufferWrite)
    private lateinit var channel: SocketChannel

    fun connect(accountName: String): Boolean {

        try {
            thread(true, true, null, "MessageReader") {
                ServerMessageReader(accountName).run()
            }
        } catch (e: Exception) {
            println("Error Connecting to server:$SERVER_HOST with port:$SERVER_PORT.")
            return false
        }
        return true

    }

    fun sendMessage(msg: OutMessage) {
        msg.build(messageBuilder)
        channel.write(bufferWrite)
        println("Message sent:${messageBuilder.printMessage()}")
        messageBuilder.clear()
    }
}