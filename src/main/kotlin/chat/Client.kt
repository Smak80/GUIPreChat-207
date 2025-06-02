package chat

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.*
import ru.smak.chat.ActionCompletionHandler
import ru.smak.chat.Communicator
import java.net.InetSocketAddress
import java.nio.channels.AsynchronousSocketChannel
import java.util.EventListener
import java.util.Scanner
import kotlin.coroutines.suspendCoroutine

class Client(
    val host: String,
    val port: Int,
) {

    private val messageListeners = mutableListOf<(String)->Unit>()
    private val socket = AsynchronousSocketChannel.open()
    private val communicator = Communicator(socket)
    private val clientScope = CoroutineScope(Dispatchers.IO)

    fun addMessageListener(listener: (String)->Unit){
        messageListeners.add(listener)
    }

    fun removeMessageListener(listener: (String)->Unit){
        messageListeners.remove(listener)
    }


    init {
        runBlocking {
            suspendCoroutine {
                socket.connect(
                    InetSocketAddress(host, port),
                    null,
                    ActionCompletionHandler(it)
                )
            }
            communicator.start(::parse)
        }
    }

    fun sendMessage(message: String) {
        clientScope.launch {
        if (message.isNotBlank()) {
            communicator.sendMessage(message)
        } else {
            stop()
        }
        }
    }

    private fun parse(message: String){
        messageListeners.forEach {
            it(message)
        }
    }

    fun stop(){
        communicator.stop()
    }
}