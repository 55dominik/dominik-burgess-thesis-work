package gb.dom55.bme.smarthomepicontroller

import android.util.Log
import com.stealthcopter.networktools.SubnetDevices
import com.stealthcopter.networktools.subnet.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.net.*
import java.util.*

object DeviceConnectionManager {

    private var isStopped = false

    private var datagramSocket = DatagramSocket(UDP_PORT)

    fun startUdpIpExchange() {
        GlobalScope.launch(Dispatchers.IO) {
            startUdpListener()
        }
    }

    fun discoverDisconnectedDevice(friendlyId: String) {
        SubnetDevices.fromLocalAddress().findDevices(
            object : SubnetDevices.OnSubnetDeviceFound{
                override fun onDeviceFound(device: Device?) {}

                override fun onFinished(devicesFound: ArrayList<Device>?) {
                    if (devicesFound != null) {
                        for (device in devicesFound) {
                            discoverUsingFriendlyAddress(InetAddress.getByName(device.ip), friendlyId)
                        }
                    }

                }

            }
        )
    }

    private fun discoverUsingFriendlyAddress(address: InetAddress, friendlyId: String) {
        GlobalScope.launch(Dispatchers.IO) {

            val tcpSocket: Socket
            try {
                tcpSocket = Socket(address, TCP_PORT)
            } catch (e: Exception) {
                return@launch
            }
            val out: OutputStream = tcpSocket.getOutputStream()
            val output = PrintWriter(out)

            try {
                output.println(DEVICE_DISCOVER_REQUEST)
                output.flush()

                val input = BufferedReader(InputStreamReader(tcpSocket.getInputStream()))
                val response = input.readLine()

                // Handle response if response exists
                response?.let{
                    if (it == friendlyId) {
                        DeviceManager.setDeviceIpAddress(address, friendlyId)
                    }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                output.close()
                out.close()
                tcpSocket.close()
            }
        }
    }

    private fun startUdpListener() {

        while(!isStopped) {
            val buffer = ByteArray(UDP_PACKET_SIZE)
            val packet = DatagramPacket(buffer, buffer.size)

            try {
                datagramSocket.receive(packet)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val buff: ByteArray = packet.data
            val stringBuffer = String(buff).replaceAfter("&end", "")

            val friendlyId = stringBuffer.subSequence(0, stringBuffer.indexOf("&end")).toString()
            if (DeviceManager.setDeviceIpAddress(packet.address, friendlyId)) {
                sendUdpResponse(packet.address)
            }
        }
    }

    private fun sendUdpResponse(address: InetAddress) {

        val localIp = getLocalIpAddress()

        localIp?.let {
            val packet = DatagramPacket(localIp.toByteArray(), localIp.length, address, UDP_PORT)
            datagramSocket.send(packet)
        }
    }

    private fun getLocalIpAddress(): String? {
        try {
            val en: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf: NetworkInterface = en.nextElement()
                val enumIpAddr: Enumeration<InetAddress> = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress: InetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.printStackTrace()
        }
        return null
    }

}