package gb.dom55.bme.smarthome.devicecontrol

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.model.AbstractDevice

class DeviceControlViewModel(application: Application) : AndroidViewModel(application) {

    val deviceData : MutableLiveData<AbstractDevice> by lazy {
        MutableLiveData<AbstractDevice>()
    }

    val deviceIdString : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private lateinit var deviceDatabaseReference : DatabaseReference
    private var valueEventListener: ValueEventListener? = null

    fun initializeData(deviceInfo: AbstractDevice) {
        deviceData.value = deviceInfo
        deviceIdString.value = deviceInfo.deviceid
        deviceDatabaseReference = FirebaseDatabase
                .getInstance().reference
                .child("devices")
                .child(deviceInfo.deviceid)
    }

    fun initializeListener() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                deviceData.value = readDeviceFromFirebase(dataSnapshot)
                deviceIdString.value = deviceData.value?.deviceid
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
    }

    fun updateValue(deviceInfo: AbstractDevice) {
        deviceData.value = deviceInfo
        writeDeviceToFirebase(deviceInfo)
    }

    fun forceUpdate() {
        deviceData.value = deviceData.value
    }

    fun stopListener() {
        valueEventListener?.let {
            deviceDatabaseReference.removeEventListener(it)
        }
    }

    fun startListener() {
        valueEventListener?.let {
            deviceDatabaseReference.addValueEventListener(it)
        }
    }

    private fun readDeviceFromFirebase(dataSnapshot: DataSnapshot) : AbstractDevice {
        return deviceData.value!!.getDataFromSnapshot(dataSnapshot)
    }

    private fun writeDeviceToFirebase(deviceInfo: AbstractDevice) {
        deviceDatabaseReference.setValue(deviceInfo.getDataClassFB())
    }

}