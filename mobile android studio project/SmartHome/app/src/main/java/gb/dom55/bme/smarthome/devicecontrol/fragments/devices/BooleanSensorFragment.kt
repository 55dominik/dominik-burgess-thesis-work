package gb.dom55.bme.smarthome.devicecontrol.fragments.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import gb.dom55.bme.smarthome.model.DeviceType
import gb.dom55.bme.smarthome.model.devices.BooleanSensor
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.model.AbstractDevice
import kotlinx.android.synthetic.main.fragment_boolean_sensor.*


class BooleanSensorFragment : DeviceControlFragmentBase() {

    private var sensor : BooleanSensor? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.fragment_boolean_sensor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.deviceData.observe(viewLifecycleOwner, Observer<AbstractDevice> {
            it?.let {
                val newReading = it as BooleanSensor
                val oldReading = sensor?: BooleanSensor()
                sensor = newReading
                if (firstRun) {
                    firstRun = false
                    setupUI()
                }
                refreshUI(oldReading, newReading)
            }
        })
    }

    private fun setupUI() {
        sensor?.let { sensor ->
            setImage(sensor.deviceType, sensor.isActive)
            setText(sensor.deviceType, sensor.isActive)

            sensorBoolName.setText(sensor.name)

            sensorBoolName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    sensor.name = sensorBoolName.text.toString()
                    model.updateValue(sensor)
                    sensorBoolName.setSelection(sensorBoolName.text.length)
                }
            }
        }
    }

    private fun refreshUI(old: BooleanSensor, new: BooleanSensor) {

        if (old.name != new.name) {
            sensorBoolName?.setText(new.name)
        }
        if (old.isActive != new.isActive) {
            setImage(new.deviceType, new.isActive)
            setText(new.deviceType, new.isActive)


        }
    }

    private fun setImage(type: DeviceType, activeImage: Boolean) {
        sensorBoolImage?.setImageResource(if (activeImage) {
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
        sensorBoolValue?.setText(if(activeText){
            when (type) {
                DeviceType.WATER_PRESENCE_SENSOR -> getString(R.string.water_present)
                DeviceType.MOTION_SENSOR -> getString(R.string.motion_detected)
                DeviceType.DOOR_SENSOR -> getString(R.string.door_open)
                else -> getString(R.string.activated)
            }
        } else {
            when (type) {
                DeviceType.WATER_PRESENCE_SENSOR -> getString(R.string.no_water)
                DeviceType.MOTION_SENSOR -> getString(R.string.no_motion)
                DeviceType.DOOR_SENSOR -> getString(R.string.door_closed)
                else -> getString(R.string.not_activated)
            }
        })
    }
}