package gb.dom55.bme.smarthome.dashboard.viewholders.devices

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.google.firebase.database.*
import com.lukelorusso.verticalseekbar.VerticalSeekBar
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.model.devices.Thermostat

class ThermostatViewHolder(itemView: View, var clickListener: DeviceItemClickListener? = null)
    : BaseViewHolder(itemView) {

    companion object {
        const val MAX_TEMP: Double = 30.0
        const val MIN_TEMP: Double = 0.0
    }

    var container: View = itemView.findViewById(R.id.dashThermostatContainer)
    var name: TextView = itemView.findViewById(R.id.dashItemThermostatName)
    private var temperatureSeekBar: VerticalSeekBar = itemView.findViewById(R.id.dashItemThermostatSeekBar)
    private var targetTemp: TextView = itemView.findViewById(R.id.dashItemThermostatTarget)

    private lateinit var deviceData: Thermostat
    private lateinit var dataRef : DatabaseReference
    var initialize: Boolean = true

    override fun setState(deviceId: String) {
        dataRef = FirebaseDatabase.getInstance().reference
                .child("devices/${deviceId}")

        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(Thermostat::class.java)?.let {
                    deviceData = it
                }
                if (initialize) { setSeekBarInitialPosition() ; initialize = false}
                updateView()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dataRef.addListenerForSingleValueEvent(dataListener)
        dataRef.addValueEventListener(dataListener)

        temperatureSeekBar.setOnProgressChangeListener { progress ->
            val newTemperature = seekBarValueToTemperature(progress)
            deviceData.targetTemperature = newTemperature
            dataRef.child("targetTemperature").setValue(deviceData.targetTemperature)
            updateView()
        }

        container.setOnClickListener { clickListener?.onDeviceItemClick(deviceData) }
    }

    private fun setSeekBarInitialPosition() {
        temperatureSeekBar.progress = temperatureToSeekBarValue(deviceData.targetTemperature)
    }

    @SuppressLint("SetTextI18n")
    // getString() does not work as intended
    private fun updateView() {
        name.text = deviceData.name
        targetTemp.text = "${deviceData.targetTemperature}°C"
    }

    private fun seekBarValueToTemperature(seekBarValue: Int): Int {
        return ((seekBarValue / 100.0) * (MAX_TEMP - MIN_TEMP) + MIN_TEMP).toInt()
    }

    private fun temperatureToSeekBarValue(temperature: Int): Int {
        return (((temperature - MIN_TEMP) / (MAX_TEMP - MIN_TEMP) * 100.0)).toInt()
    }
}