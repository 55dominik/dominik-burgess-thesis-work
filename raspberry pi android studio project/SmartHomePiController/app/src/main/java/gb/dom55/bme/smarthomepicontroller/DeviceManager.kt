package gb.dom55.bme.smarthomepicontroller

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import gb.dom55.bme.smarthome.Model.Devices.BooleanSensor
import gb.dom55.bme.smarthome.Model.Devices.DimmableLight
import gb.dom55.bme.smarthome.Model.Devices.IntegerSensor
import gb.dom55.bme.smarthome.Model.Devices.Light
import gb.dom55.bme.smarthomepicontroller.model.AbstractDevice
import gb.dom55.bme.smarthomepicontroller.model.DeviceType
import gb.dom55.bme.smarthomepicontroller.model.devices.CoffeeMaker
import gb.dom55.bme.smarthomepicontroller.model.devices.IrRemote
import gb.dom55.bme.smarthomepicontroller.model.devices.RgbLight
import gb.dom55.bme.smarthomepicontroller.model.devices.Thermostat
import org.json.JSONObject
import java.net.InetAddress

object DeviceManager {
    private val devices = mutableListOf<AbstractDevice>()
    private const val TAG = "DeviceManager"

    fun addDeviceFromUserNode(snapshot: DataSnapshot) {
        if (!snapshot.child("id").exists() ||
            !snapshot.child("connection").exists() ||
            !snapshot.child("type").exists()) {
            return
        }

        val deviceId = snapshot.child("id").getValue(String::class.java)!!
        val friendlyId = snapshot.child("connection").getValue(String::class.java)!!
        val type = snapshot.child("type").getValue(Int::class.java)!!


        if (devices.any {it.deviceid == deviceId}) { return } // Device already exists


        var factoryDevice: AbstractDevice? = null
        DeviceType.values().find { it.type == type }?.let { deviceType ->
            factoryDevice = when(deviceType) {
                DeviceType.RGBLIGHT -> RgbLight(deviceId, friendlyId)
                DeviceType.LIGHT -> Light(deviceId, friendlyId)
                DeviceType.DIMMABLE_LIGHT -> DimmableLight(deviceId, friendlyId)
                DeviceType.BOOL_SENSOR -> BooleanSensor(deviceId, friendlyId)
                DeviceType.INT_SENSOR -> IntegerSensor(deviceId, friendlyId)
                DeviceType.NULL_DEVICE -> null
                DeviceType.KETTLE -> Light(deviceId, friendlyId)
                DeviceType.SOCKET -> Light(deviceId, friendlyId)
                DeviceType.AIR_CON -> Light(deviceId, friendlyId)
                DeviceType.DOOR_LOCK -> Light(deviceId, friendlyId)
                DeviceType.IR_REMOTE -> IrRemote(deviceId, friendlyId)
                DeviceType.RGB_IR_REMOTE -> IrRemote(deviceId, friendlyId)
                DeviceType.TV_REMOTE -> IrRemote(deviceId, friendlyId)
                DeviceType.COFFEE -> CoffeeMaker(deviceId, friendlyId)
                DeviceType.BLINDS -> DimmableLight(deviceId, friendlyId)
                DeviceType.RADIATOR -> DimmableLight(deviceId, friendlyId)
                DeviceType.DOOR_SENSOR -> BooleanSensor(deviceId, friendlyId)
                DeviceType.MOTION_SENSOR -> BooleanSensor(deviceId, friendlyId)
                DeviceType.WATER_PRESENCE_SENSOR -> BooleanSensor(deviceId, friendlyId)
                DeviceType.CELSIUS_SENSOR -> IntegerSensor(deviceId, friendlyId)
                DeviceType.HUMIDITY_SENSOR -> IntegerSensor(deviceId, friendlyId)
                DeviceType.WATER_LEVEL_SENSOR -> IntegerSensor(deviceId, friendlyId)
                DeviceType.MOISTURE_SENSOR -> IntegerSensor(deviceId, friendlyId)
                DeviceType.THERMOSTAT -> Thermostat(deviceId, friendlyId)
            }
        }
        factoryDevice?.let {
            getDeviceFromDeviceNode(it)
        }

    }

    // Returns false when device does not exist
    fun setDeviceIpAddress(address: InetAddress, friendlyId: String): Boolean {
        val device = devices.firstOrNull { device -> device.friendlyId == friendlyId }

        return if (device == null) {
            false
        } else {
            device.ip = address
            true
        }
    }

    private fun getDeviceFromDeviceNode(device: AbstractDevice) {
        device.databaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    device.setDeviceDataFromSnapshot(snapshot)
                    device.createValueEventListeners()
                    devices.add(device)
                }

                override fun onCancelled(p0: DatabaseError) {
                    p0.toException().printStackTrace()
                }

            })
    }

    fun removeDevice(snapshot: DataSnapshot) {
        if (!snapshot.child("id").exists()){
            return
        }
        devices.find {
            it.deviceid == snapshot.child("id").getValue(String::class.java)
        }?.let { absDevice ->
            absDevice.dataListener?.let { listener ->
                absDevice.databaseReference.removeEventListener(listener)
            }
            devices.remove(absDevice)
        }
    }

    fun updateDevice(friendlyId: String, jsonString: String) {
        val device = devices.firstOrNull {
            it.friendlyId == friendlyId
        } ?: return

        val receivedJson = JSONObject(jsonString)
        val deviceJson = JSONObject(device.getJsonString())

        for (item in receivedJson.keys()) {
            deviceJson.put(item, receivedJson[item])
        }
        device.setFromJsonString(deviceJson.toString())
    }

    fun deviceCount() = devices.size

}