package gb.dom55.bme.smarthomepicontroller

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.net.ServerSocket
import java.net.Socket

object DeviceDataReceiver {

    private var serverSocket = ServerSocket(TCP_PORT)

    var isStopped = false

    fun startTcpServer() {

        while(!isStopped) {
            try {
                handleConnectionOnIOThread(serverSocket.accept())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleConnectionOnIOThread(connection: Socket) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val input = BufferedReader(InputStreamReader(connection.getInputStream()))
                val received = input.readLine()
                val indexOfLastJsonChar = received.lastIndexOf('}')
                val jsonString = received.substring(0, indexOfLastJsonChar + 1)
                val friendlyId = received.substring(indexOfLastJsonChar + 1)
                DeviceManager.updateDevice(friendlyId, jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally { 
                connection.close()
            }
        }
    }


}