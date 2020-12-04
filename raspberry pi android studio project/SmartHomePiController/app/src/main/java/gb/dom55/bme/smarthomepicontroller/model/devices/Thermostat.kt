package gb.dom55.bme.smarthomepicontroller.model.devices

import com.google.firebase.database.DataSnapshot
import gb.dom55.bme.smarthome.Model.Devices.DimmableLight
import gb.dom55.bme.smarthome.Model.Devices.IntegerSensor
import gb.dom55.bme.smarthomepicontroller.model.AbstractDevice
import gb.dom55.bme.smarthomepicontroller.model.DeviceType
import gb.dom55.bme.smarthomepicontroller.model.FirebaseDevice
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class Thermostat(deviceid: String, connectionCode: String)
    : AbstractDevice(deviceid, connectionCode) {

    override var deviceData: FirebaseDevice = FirebaseThermostat()

    override fun setDeviceDataFromSnapshot(snapshot: DataSnapshot) {
        deviceData = snapshot.getValue(FirebaseThermostat::class.java)!!
    }

    override fun getJsonString(): String {
        return Json(JsonConfiguration.Stable).stringify(
            FirebaseThermostat.serializer(),
            deviceData as FirebaseThermostat
        )
    }

    override fun setFirebase() {
        databaseReference.setValue(deviceData)
        databaseReference.child("temperature")
            .setValue((deviceData as FirebaseThermostat).temperature)
    }

    override fun setFromJsonString(jsonString: String) {
        deviceData = Json(JsonConfiguration.Stable)
            .parse(FirebaseThermostat.serializer(), jsonString)
        checkForNotifications()
        setFirebase()
    }

    override fun getIntegerPropertyFromString(propertyName: String): Int? {
        val data = deviceData as FirebaseThermostat
        return when (propertyName) {
            "temperature" -> data.temperature
            else -> null
        }
    }

    @Serializable
    class FirebaseThermostat : FirebaseDevice() {
        var uid: String =""
        var deviceid: String=""
        var name: String=""
        var deviceType: DeviceType = DeviceType.NULL_DEVICE

        var targetTemperature: Int = 0
        var temperature: Int = 0
    }
}