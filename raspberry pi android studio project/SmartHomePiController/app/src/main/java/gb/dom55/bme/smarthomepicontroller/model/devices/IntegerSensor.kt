package gb.dom55.bme.smarthome.Model.Devices

import com.google.firebase.database.DataSnapshot
import gb.dom55.bme.smarthomepicontroller.model.FirebaseDevice
import gb.dom55.bme.smarthomepicontroller.model.AbstractDevice
import gb.dom55.bme.smarthomepicontroller.model.DeviceType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class IntegerSensor(deviceid: String, connectionCode: String)
    : AbstractDevice(deviceid, connectionCode){

    override var deviceData: FirebaseDevice = FirebaseIntegerSensor()

    override fun setDeviceDataFromSnapshot(snapshot: DataSnapshot) {
        deviceData = snapshot.getValue(FirebaseIntegerSensor::class.java)!!
    }

    override fun sendDataToDevice() {
        // No operation
    }

    override fun getJsonString(): String {
        return Json(JsonConfiguration.Stable).stringify(
            FirebaseIntegerSensor.serializer(),
            deviceData as FirebaseIntegerSensor
        )
    }

    override fun setFirebase() {
        databaseReference.child("value").setValue(
            (deviceData as FirebaseIntegerSensor).value
        )
    }

    override fun setFromJsonString(jsonString: String) {
        deviceData = Json(JsonConfiguration.Stable)
            .parse(FirebaseIntegerSensor.serializer(), jsonString)
        checkForNotifications()
        setFirebase()
    }

    override fun getIntegerPropertyFromString(propertyName: String): Int? {
        val data = deviceData as FirebaseIntegerSensor
        return when (propertyName) {
            "value" -> data.value
            else -> null
        }
    }

    @Serializable
    class FirebaseIntegerSensor : FirebaseDevice() {
        var uid: String =""
        var deviceid: String=""
        var name: String=""
        var deviceType: DeviceType = DeviceType.NULL_DEVICE

        var value: Int=0
    }

}