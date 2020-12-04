package gb.dom55.bme.smarthome.Model.Devices

import com.google.firebase.database.DataSnapshot
import gb.dom55.bme.smarthomepicontroller.model.AbstractDevice
import gb.dom55.bme.smarthomepicontroller.model.DeviceType
import gb.dom55.bme.smarthomepicontroller.model.FirebaseDevice
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class Light(deviceid: String, connectionCode: String)
    : AbstractDevice(deviceid, connectionCode) {

    override var deviceData: FirebaseDevice = FirebaseLight()

    override fun setDeviceDataFromSnapshot(snapshot: DataSnapshot) {
        deviceData = snapshot.getValue(FirebaseLight::class.java)!!
    }

    override fun getJsonString(): String {
        return Json(JsonConfiguration.Stable).stringify(
            FirebaseLight.serializer(),
            deviceData as FirebaseLight
        )
    }

    override fun setFromJsonString(jsonString: String) {
        deviceData = Json(JsonConfiguration.Stable)
            .parse(FirebaseLight.serializer(), jsonString)
        setFirebase()
    }

    @Serializable
    class FirebaseLight: FirebaseDevice() {
        var uid: String =""
        var deviceid: String=""
        var name: String=""
        var deviceType: DeviceType = DeviceType.NULL_DEVICE

        var on: Boolean = false
    }
}