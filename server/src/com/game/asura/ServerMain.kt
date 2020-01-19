package com.game.asura

import com.game.asura.account.*
import com.game.asura.card.CardInfoStore
import com.game.asura.messaging.MessageField
import com.game.asura.processor.InMessageProcessor
import com.game.asura.processor.MessageDecoder
import com.game.asura.processor.MessageProcessor
import com.game.asura.processor.OutMessageProcessor
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import kotlin.concurrent.thread

class ServerMain {

    inner class MainLogicProcessor : Runnable {

        private val matchFinder = MatchFinder()
        private val cardInfoStore = CardInfoStore()
        private val inProcessor = InMessageProcessor(messageQueue, accountCache, matchFinder,cardInfoStore)
        private val outProcessor = OutMessageProcessor()
        private val messageProcessor = MessageProcessor(inProcessor, outProcessor)

        override fun run() {
            while (true) {
                val message = messageQueue.take()
                messageProcessor.onMessage(message)
            }
        }

    }


    private val SERVER_HOST = "localhost"
    private val SERVER_PORT = 8555
    private val selector = Selector.open()
    private val socketChannel = ServerSocketChannel.open()
    private val messageQueue = BlockingMessageQueue()
    private val messageDecoder = MessageDecoder(messageQueue)
    private val accountCache = AccountCache()

    fun start() {

        thread(true, true, null, "CoreLogic") {
            MainLogicProcessor().run()
        }
        setup()
        println("Server starting to accept client...")

        while (true) {
            selector.select()
            val selectedKeys = selector.selectedKeys()
            val itr = selectedKeys.iterator()
            while (itr.hasNext()) {
                val key = itr.next()
                //a key may have multiple ops rdy
                if (key.isAcceptable) {
                    onNewConnectionRecv(selector, socketChannel)
                }
                if (key.isReadable) {
                    onMessageReceived(key)
                }
                itr.remove()
            }
        }
    }

    private fun onMessageReceived(key: SelectionKey) {
        val client = key.channel() as SocketChannel
        val clientInet = client.remoteAddress as InetSocketAddress
        val accountKey = clientInet.toString()

        if (!accountCache.isValid(accountKey)) {
            println("Invalid client $clientInet , it is neither pending nor in active connection state.")
            return
        }
        val currAccount: Account? = accountCache.getAccount(accountKey)
        if (currAccount == null) {
            setupPlayerAccount(client, accountKey)
            return
        }
        val channelInfo = accountCache.getClientChannelInfo(accountKey) ?: return
        readMessage(currAccount, channelInfo)
    }

    private fun setupPlayerAccount(client: SocketChannel, accountKey: String) {
        //no player found so let's see see if its a login message so we can create it.

        val readBuffer = ByteBuffer.allocate(1460)
        val writeBuffer = ByteBuffer.allocate(1460)
        val channelWriter = ChannelMessageWriter(client, writeBuffer)
        val channelInfo = ClientChannelInfo(client, readBuffer)
        channelInfo.readIntoBuffer()
        val tokenizer = channelInfo.tokenizer
        if (!tokenizer.hasField(MessageField.ACCOUNT_NAME)) {
            println("Message recv:${tokenizer.printMessage()}.")
            println("Unable to create playerAccount since account name field is missing from message.")
            disconnect(client, accountKey)
            return
        }
        val accountName = tokenizer.nextValue() as String
        val playerAccount = PlayerAccount(channelInfo, channelWriter, accountKey, accountName)
        tokenizer.rewind()
        //process login , don't want to add to map until login is validated

        //don't want to read message again since channel data has been read into buffer already
        messageDecoder.decode(playerAccount)
    }

    private fun disconnect(clientChannel: SocketChannel, accountKey: String) {
        println("Disconnecting:$accountKey and removing it from active/pending cache.")
        accountCache.removePendingAccount(accountKey)
        accountCache.removeActiveAccount(accountKey)
        clientChannel.close()
    }

    private fun readMessage(account: Account, clientChannelInfo: ClientChannelInfo) {
        try {
            val byteRead = clientChannelInfo.readIntoBuffer()
            if (byteRead == 0) {
                println("ERROR:Read 0 bytes")
                return
            }

        } catch (exception: Exception) {
            println("Exception:$exception player:${account.getAccountName()} has disconnected from server.")
            clientChannelInfo.close()
            return
        }
        messageDecoder.decode(account)
    }

    private fun setup() {
        val socketAddress = InetSocketAddress(SERVER_HOST, SERVER_PORT)
        socketChannel.bind(socketAddress)
        socketChannel.configureBlocking(false)
        socketChannel.register(selector, SelectionKey.OP_ACCEPT)
    }

    private fun onNewConnectionRecv(selector: Selector, socketChannel: ServerSocketChannel) {
        val client = socketChannel.accept()
        val clientInet = client.remoteAddress as InetSocketAddress
        println("Connection Info:${client.remoteAddress}")
        client.configureBlocking(false)
        client.register(selector, SelectionKey.OP_READ)
        accountCache.addPendingAccount(clientInet.toString(), client)
    }
}


fun main(arg: Array<String>) {

    ServerMain().start()
}