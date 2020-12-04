package gb.dom55.bme.smarthome.Model.Devices

import com.google.firebase.database.DataSnapshot
import gb.dom55.bme.smarthomepicontroller.model.AbstractDevice
import gb.dom55.bme.smarthomepicontroller.model.FirebaseDevice

class NullDevice : AbstractDevice("", "") {
    override var deviceData = FirebaseDevice()
    override fun setDeviceDataFromSnapshot(snapshot: DataSnapshot) {}
    override fun setFirebase() {}
    override fun getJsonString(): String = "null"
    override fun setFromJsonString(jsonString: String) {}
}