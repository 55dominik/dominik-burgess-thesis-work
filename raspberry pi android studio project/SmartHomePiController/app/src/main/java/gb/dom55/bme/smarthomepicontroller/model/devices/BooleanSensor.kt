package gb.dom55.bme.smarthome.Model.Devices

import com.google.firebase.database.DataSnapshot
import gb.dom55.bme.smarthomepicontroller.model.FirebaseDevice
import gb.dom55.bme.smarthomepicontroller.model.AbstractDevice
import gb.dom55.bme.smarthomepicontroller.model.DeviceType
import gb.dom55.bme.smarthomepicontroller.model.devices.CoffeeMaker
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class BooleanSensor(deviceid: String, connectionCode: String)
    : AbstractDevice(deviceid, connectionCode){

    override var deviceData: FirebaseDevice = FirebaseBooleanSensor()

    override fun setDeviceDataFromSnapshot(snapshot: DataSnapshot) {
        deviceData = snapshot.getValue(FirebaseBooleanSensor::class.java)!!
    }

    override fun sendDataToDevice() {
        // No operation
    }

    override fun getJsonString(): String {
        return Json(JsonConfiguration.Stable).stringify(
            FirebaseBooleanSensor.serializer(),
            deviceData as FirebaseBooleanSensor
        )
    }

    override fun setFromJsonString(jsonString: String) {
        deviceData = Json(JsonConfiguration.Stable)
            .parse(FirebaseBooleanSensor.serializer(), jsonString)
        checkForNotifications()
        setFirebase()
    }

    override fun getBooleanPropertyFromString(propertyName: String): Boolean? {
        val data = deviceData as FirebaseBooleanSensor
        return when (propertyName) {
            "isActive", "active" -> data.isActive
            else -> null
        }
    }

    @Serializable
    class FirebaseBooleanSensor : FirebaseDevice() {
        var uid: String =""
        var deviceid: String=""
        var name: String=""
        var deviceType: DeviceType = DeviceType.NULL_DEVICE

        var isActive: Boolean = false
    }

}