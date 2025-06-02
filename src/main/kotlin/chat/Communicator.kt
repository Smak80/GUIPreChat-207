package ru.smak.chat

import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousSocketChannel
import java.util.*
import kotlin.coroutines.suspendCoroutine


class Communicator(
    val socket: AsynchronousSocketChannel,
) {
    private var parse: ((String)->Unit)? = null
    private val communicatorScope = CoroutineScope(Dispatchers.IO)
    var isRunnig = false
        private set
    //private val scanner = Scanner(socket.getInputStream())
    //private val writer = PrintWriter(socket.getOutputStream())

    private fun startMessageAccepting(){
        communicatorScope.launch {
            while(isRunnig){
                try {
                    var capacity = Int.SIZE_BYTES
                    repeat(2){
                        val buf = ByteBuffer.allocate(capacity)
                        suspendCoroutine {
                            socket.read(buf,
                                null,
                                ActionCompletionHandler(it))
                        }
                        buf.flip()
                        if (it == 0) capacity = buf.getInt()
                        else {
                            val data = Charsets.UTF_8.decode(buf).toString()
                            parse?.invoke(data)
                        }
                    }

                } catch (_: Throwable){
                    break
                }
            }
        }
    }

    suspend fun sendMessage(message: String){
        val ba = message.toByteArray()
        val buf = ByteBuffer.allocate(ba.size + Int.SIZE_BYTES)
        buf.putInt(ba.size)
        buf.put(ba)
        buf.flip()
        val wrote = suspendCoroutine{
            socket.write(
                buf,
                null,
                ActionCompletionHandler(it)
            )
        }
    }

    fun start(parser: (String)->Unit){
        if (!socket.isOpen) throw Exception("Client disconnected")
        parse = parser
        if (!isRunnig) {
            isRunnig = true
            startMessageAccepting()
        }
    }

    fun stop(){
        isRunnig = false
        socket.close()
    }
}