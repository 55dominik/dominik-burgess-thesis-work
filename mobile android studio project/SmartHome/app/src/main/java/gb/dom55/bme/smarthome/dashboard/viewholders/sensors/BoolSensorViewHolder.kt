package gb.dom55.bme.smarthome.dashboard.viewholders.sensors

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.dashboard.viewholders.devices.BaseViewHolder
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.model.DeviceType
import gb.dom55.bme.smarthome.model.devices.BooleanSensor
import gb.dom55.bme.smarthome.R

@Suppress("UNNECESSARY_SAFE_CALL")
class BoolSensorViewHolder(itemView: View, var clickListener: DeviceItemClickListener? = null) : BaseViewHolder(itemView) {

    var value: TextView = itemView.findViewById(R.id.statusBoolValue)
    var name: TextView = itemView.findViewById(R.id.statusBoolName)
    var image: ImageView = itemView.findViewById(R.id.statusBoolImage)
    var container: View = itemView.findViewById(R.id.statusBoolContainer)
    private lateinit var deviceData: BooleanSensor
    private lateinit var dataRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun setState(deviceId: String) {
        auth = FirebaseAuth.getInstance()
        dataRef = FirebaseDatabase.getInstance().reference
                .child("devices/${deviceId}")

        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(BooleanSensor::class.java)?.let {
                    deviceData = it
                    name.setText(it.name)
                    setImage(it.deviceType, false)
                }
                updateView()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dataRef.addListenerForSingleValueEvent(dataListener)
        dataRef.addValueEventListener(dataListener)

        container.setOnClickListener {
            clickListener?.onDeviceItemClick(deviceData)
        }

    }

    private fun updateView() {

        setImage(deviceData.deviceType, deviceData.isActive)
        setText(deviceData.deviceType, deviceData.isActive)
        name.setText(deviceData.name)
    }

    private fun setImage(type: DeviceType, activeImage: Boolean) {
        image.setImageResource(if (activeImage) {
            when (type) {
                DeviceType.DOOR_SENSOR -> R.drawable.ic_door_active
                DeviceType.MOTION_SENSOR -> R.drawable.ic_movement_active
                DeviceType.WATER_PRESENCE_SENSOR -> R.drawable.ic_water_present
                else -> R.drawable.sensor_icon
            }
        } else {
            when (type) {
                DeviceType.DOOR_SENSOR -> R.drawable.ic_door_inactive
                DeviceType.MOTION_SENSOR -> R.drawable.ic_movement_inactive
                DeviceType.WATER_PRESENCE_SENSOR -> R.drawable.ic_water_low
                else -> R.drawable.sensor_icon
            }
        })
    }

    private fun setText(type: DeviceType, activeText: Boolean) {
        value?.setText(if(activeText){
            when (type) {
                DeviceType.WATER_PRESENCE_SENSOR -> "Water present"
                DeviceType.MOTION_SENSOR -> "Motion detected"
                DeviceType.DOOR_SENSOR -> "Door open"
                else -> "Activated"
            }
        } else {
            when (type) {
                DeviceType.WATER_PRESENCE_SENSOR -> "No water"
                DeviceType.MOTION_SENSOR -> "No motion"
                DeviceType.DOOR_SENSOR -> "Door closed"
                else -> "Inactive"
            }
        })
    }


}