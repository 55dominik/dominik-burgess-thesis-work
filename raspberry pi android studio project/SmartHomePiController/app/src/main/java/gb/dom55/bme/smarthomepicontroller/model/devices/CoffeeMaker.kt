package gb.dom55.bme.smarthomepicontroller.model.devices

import com.google.firebase.database.DataSnapshot
import gb.dom55.bme.smarthomepicontroller.model.AbstractDevice
import gb.dom55.bme.smarthomepicontroller.model.DeviceType
import gb.dom55.bme.smarthomepicontroller.model.FirebaseDevice
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class CoffeeMaker(deviceid: String, connectionCode: String)
    : AbstractDevice(deviceid, connectionCode) {

    override var deviceData: FirebaseDevice = FirebaseCoffeeMaker()

    override fun setDeviceDataFromSnapshot(snapshot: DataSnapshot) {
        deviceData = snapshot.getValue(FirebaseCoffeeMaker::class.java)!!
    }

    override fun getJsonString(): String {
        return Json(JsonConfiguration.Stable).stringify(
            FirebaseCoffeeMaker.serializer(),
            deviceData as FirebaseCoffeeMaker
        )
    }

    override fun setFromJsonString(jsonString: String) {
        deviceData = Json(JsonConfiguration.Stable)
            .parse(FirebaseCoffeeMaker.serializer(), jsonString)
        checkForNotifications()
        setFirebase()
    }

    override fun getBooleanPropertyFromString(propertyName: String): Boolean? {
        val data = deviceData as FirebaseCoffeeMaker
        return when (propertyName) {
            "cupPresent" -> data.cupPresent
            "enoughWater" -> data.enoughWater
            else -> null
        }
    }

    @Suppress("unused")
    @Serializable
    class FirebaseCoffeeMaker : FirebaseDevice() {
        var uid: String =""
        var deviceid: String=""
        var name: String=""
        var deviceType: DeviceType = DeviceType.NULL_DEVICE

        var enoughWater: Boolean = false
        var cupPresent: Boolean = false
        var makeCoffee: Boolean = false
    }
}