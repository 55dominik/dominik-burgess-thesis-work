package gb.dom55.bme.smarthome.dashboard.viewholders.sensors

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.dashboard.viewholders.devices.BaseViewHolder
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.model.devices.DeviceType
import gb.dom55.bme.smarthome.model.devices.IntegerSensor
import gb.dom55.bme.smarthome.R

class IntSensorViewHolder(itemView: View, var clickListener: DeviceItemClickListener? = null) : BaseViewHolder(itemView) {

    var value: TextView = itemView.findViewById(R.id.statusIntValue)
    var name: TextView = itemView.findViewById(R.id.statusIntName)
    var container: View = itemView.findViewById(R.id.statusIntContainer)
    private var image: ImageView = itemView.findViewById(R.id.statusIntImage)

    private lateinit var deviceData: IntegerSensor
    private lateinit var dataRef : DatabaseReference

    override fun setState(deviceId: String) {
        dataRef = FirebaseDatabase.getInstance().reference
                .child("devices/${deviceId}")

        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(IntegerSensor::class.java)?.let {
                    deviceData = it
                    setImage(it.deviceType)
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
        setText(deviceData.deviceType, deviceData.value)
        name.setText(deviceData.name)
    }

    private fun setImage(type: DeviceType) {
        image.setImageResource(
                when (type) {
                    DeviceType.MOISTURE_SENSOR -> R.drawable.ic_moisture
                    DeviceType.WATER_LEVEL_SENSOR -> R.drawable.ic_water_level
                    DeviceType.HUMIDITY_SENSOR -> R.drawable.ic_humidity
                    DeviceType.CELSIUS_SENSOR -> R.drawable.ic_celsius
                    else -> R.drawable.integer_sensor_icon
                }
        )
    }

    private fun setText(type: DeviceType, value: Int) {
        this.value.text = when (type) {
            DeviceType.MOISTURE_SENSOR,
            DeviceType.WATER_LEVEL_SENSOR,
            DeviceType.HUMIDITY_SENSOR -> "$value%"
            DeviceType.CELSIUS_SENSOR -> "$value°C"
            else -> value.toString()
        }
    }


}
