package gb.dom55.bme.smarthomepicontroller.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import gb.dom55.bme.smarthomepicontroller.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthomepicontroller.DeviceConnectionManager
import gb.dom55.bme.smarthomepicontroller.TCP_PORT
import gb.dom55.bme.smarthomepicontroller.model.notification.BooleanNotificationProperty
import gb.dom55.bme.smarthomepicontroller.model.notification.IntegerNotificationProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import java.net.ConnectException
import java.net.InetAddress
import java.net.NoRouteToHostException
import java.net.Socket

abstract class AbstractDevice(
    var deviceid: String,
    var friendlyId: String
) {

    var ip: InetAddress? = null

    val userId = FirebaseAuth.getInstance().currentUser?.uid!!

    val databaseReference = FirebaseDatabase.getInstance()
        .reference
        .child("devices")
        .child(deviceid)

    val notificationsReference = FirebaseDatabase.getInstance()
        .reference
        .child("notifications")
        .child(deviceid)

    val notificationsForwarder = FirebaseDatabase.getInstance()
        .reference
        .child("notificationForwarder")
        .child(deviceid)

    var dataListener:  ValueEventListener? = null
    abstract var deviceData: FirebaseDevice
    val notifications = mutableListOf<NotificationPropertyBase>()


    fun createValueEventListeners() {
        dataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                setDeviceDataFromSnapshot(snapshot)
                sendDataToDevice()
            }

            override fun onCancelled(p0: DatabaseError) {
                p0.toException().printStackTrace()
            }

        }
        dataListener?.let { databaseReference.addValueEventListener(it) }
        createNotificationsEventListener()
    }

    private fun createNotificationsEventListener() {
        notificationsReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    return
                }
                notifications.clear()
                for (notifSnapshot in snapshot.children) {
                    val type = notifSnapshot.child("type").getValue(String::class.java)
                    if (type == "boolean") {
                        notifications.add(BooleanNotificationProperty.getFromFirebase(notifSnapshot))
                    }
                    if (type == "integer") {
                        notifications.add(IntegerNotificationProperty.getFromFirebase(notifSnapshot))
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                if (p0.code == DatabaseError.PERMISSION_DENIED) {
                    Log.d("Notifications", "Device has no notifications")
                }
            }
        })
    }

    abstract fun setDeviceDataFromSnapshot(snapshot: DataSnapshot)

    open fun setFirebase() {
        databaseReference.setValue(deviceData)
    }

    protected open fun sendDataToDevice() {
        if (ip == null) return
        GlobalScope.launch(Dispatchers.IO) {
            val tcpSocket: Socket
            try {
                tcpSocket = Socket(ip, TCP_PORT)
            } catch (e: NoRouteToHostException) {
                DeviceConnectionManager.discoverDisconnectedDevice(friendlyId)
                return@launch
            } catch (e: ConnectException) {
                return@launch
            }

            val out: OutputStream = tcpSocket.getOutputStream()
            val output = PrintWriter(out)

            try {

                output.println(getJsonString())
                output.flush()

                val input = BufferedReader(InputStreamReader(tcpSocket.getInputStream()))
                val response = input.readLine()

                response?.let{
                    Log.d("Device", "Data sent, response was: $it")
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
    abstract fun getJsonString(): String
    abstract fun setFromJsonString(jsonString: String)

    open fun checkForNotifications() {
        loop@ for (item in notifications) {
            when (item) {
                is BooleanNotificationProperty -> {
                    if (!item.isActive) continue@loop

                    val check = getBooleanPropertyFromString(item.devicePropertyNodeName)
                    check?.let { value ->
                        if (item.notifyWhenTrue && value) {
                            sendNotification(item.propertyName, item.trueMessage)
                        }
                        if (item.notifyWhenFalse && !value) {
                            sendNotification(item.propertyName, item.falseMessage)
                        }
                    }
                }
                is IntegerNotificationProperty -> {
                    if (!item.isActive) continue@loop

                    val check = getIntegerPropertyFromString(item.devicePropertyNodeName)
                    check?.let {
                        val conditionsMet = when (item.triggerCondition) {
                            "LT" -> check < item.triggerValue
                            "EQ" -> check == item.triggerValue
                            "GT" -> check > item.triggerValue
                            else -> false
                        }
                        if (conditionsMet) {
                            sendNotification(item.propertyName, item.message)
                        }
                    }
                }
            }
        }
    }

    open fun getBooleanPropertyFromString(propertyName: String): Boolean? { return null }
    open fun getIntegerPropertyFromString(propertyName: String): Int? { return null }

    private fun sendNotification(notificationHeader: String, notificationMessage: String) {
        // Removal of notification from firebase not necessary here
        // The set notification value will be removed by Firebase Cloud Functions
        // to be able to listen for changes to same node in the future.

        notificationsForwarder.setValue(object {
            val notificationHeader = notificationHeader
            val notificationMessage = notificationMessage
            val uid = userId
        })
    }
}