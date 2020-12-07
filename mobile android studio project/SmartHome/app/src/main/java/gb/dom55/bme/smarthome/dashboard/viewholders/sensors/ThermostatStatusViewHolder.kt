package gb.dom55.bme.smarthome.dashboard.viewholders.sensors

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.dashboard.viewholders.devices.BaseViewHolder
import gb.dom55.bme.smarthome.model.devices.Thermostat

class ThermostatStatusViewHolder(itemView: View, var clickListener: DeviceItemClickListener? = null)
    : BaseViewHolder(itemView) {

    var currentTemperature: TextView = itemView.findViewById(R.id.statusThermostatValue)
    var name: TextView = itemView.findViewById(R.id.statusThermostatName)
    var container: View = itemView.findViewById(R.id.statusThermostatContainer)

    private lateinit var deviceData: Thermostat
    private lateinit var dataRef : DatabaseReference

    override fun setState(deviceId: String) {
        dataRef = FirebaseDatabase.getInstance().reference
                .child("devices/${deviceId}")

        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(Thermostat::class.java)?.let {
                    deviceData = it
                    updateView()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dataRef.addListenerForSingleValueEvent(dataListener)
        dataRef.addValueEventListener(dataListener)

        container.setOnClickListener { clickListener?.onDeviceItemClick(deviceData) }

    }

    @SuppressLint("SetTextI18n")
    // getString() does not work as intended
    private fun updateView() {
        currentTemperature.text = "${deviceData.temperature}°C"
        name.setText(deviceData.name)
    }

}