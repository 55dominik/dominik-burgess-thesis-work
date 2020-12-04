package gb.dom55.bme.smarthomepicontroller.model.devices

import com.google.firebase.database.DataSnapshot
import gb.dom55.bme.smarthomepicontroller.model.AbstractDevice
import gb.dom55.bme.smarthomepicontroller.model.FirebaseDevice
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class IrRemote(deviceid: String, connectionCode: String)
    : AbstractDevice(deviceid, connectionCode) {

    override var deviceData: FirebaseDevice = FirebaseIrRemote()

    override fun setDeviceDataFromSnapshot(snapshot: DataSnapshot) {
        snapshot.child("currentButton")
            .getValue(String::class.java)
            ?.let { (deviceData as FirebaseIrRemote).currentButton = it }
    }

    override fun setFirebase() {
        // Super class function not compatible with IR remote's functionality
    }

    override fun getJsonString(): String {
        return Json(JsonConfiguration.Stable).stringify(
            FirebaseIrRemote.serializer(),
            deviceData as FirebaseIrRemote
        )
    }

    override fun setFromJsonString(jsonString: String) {
        deviceData = Json(JsonConfiguration.Stable)
            .parse(FirebaseIrRemote.serializer(), jsonString)
        setFirebase()
    }

    @Serializable
    class FirebaseIrRemote: FirebaseDevice() {
        var currentButton: String = ""
    }

}