package gb.dom55.bme.smarthomepicontroller.model.devices

import com.google.firebase.database.*
import gb.dom55.bme.smarthomepicontroller.model.AbstractDevice
import gb.dom55.bme.smarthomepicontroller.model.DeviceType
import gb.dom55.bme.smarthomepicontroller.model.FirebaseDevice
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class RgbLight(deviceid: String, friendlyId: String)
    : AbstractDevice(deviceid, friendlyId) {

    override var deviceData: FirebaseDevice = FirebaseRgbLight()

    override fun setDeviceDataFromSnapshot(snapshot: DataSnapshot) {
        deviceData = snapshot.getValue(FirebaseRgbLight::class.java)!!
    }

    override fun getJsonString(): String {
        return Json(JsonConfiguration.Stable).stringify(
            FirebaseRgbLight.serializer(),
            deviceData as FirebaseRgbLight
        )
    }

    override fun setFromJsonString(jsonString: String) {
        deviceData = Json(JsonConfiguration.Stable)
            .parse(FirebaseRgbLight.serializer(), jsonString)
        setFirebase()
    }

    @Serializable
    class FirebaseRgbLight : FirebaseDevice() {
        var uid: String =""
        var deviceid: String=""
        var name: String=""
        var deviceType: DeviceType = DeviceType.NULL_DEVICE

        var on: Boolean=true
        var white: Boolean=true
        var brightness: Double=10.0
        var red: Int=1
        var green: Int=1
        var blue: Int=1
    }
}