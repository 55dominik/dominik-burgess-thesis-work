package gb.dom55.bme.smarthome.devicecontrol.fragments.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import gb.dom55.bme.smarthome.model.devices.DeviceType
import gb.dom55.bme.smarthome.model.devices.IntegerSensor
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.model.devices.AbstractDevice
import kotlinx.android.synthetic.main.fragment_integer_sensor.*


class IntegerSensorFragment : DeviceControlFragmentBase() {

    private var sensor : IntegerSensor? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.fragment_integer_sensor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.deviceData.observe(viewLifecycleOwner, Observer<AbstractDevice> {
            val newReading = it as IntegerSensor
            val oldReading = sensor?: IntegerSensor()
            sensor = newReading
            if (firstRun) {
                firstRun = false
                setupUI()
            }
            refreshUI(oldReading, newReading)
        })

    }

    private fun setupUI() {
        sensor?.let { sensor ->
            setText(sensor.deviceType, sensor.value)
            setImage(sensor.deviceType)
            sensorIntegerName.setText(sensor.name)

            sensorIntegerName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    sensor.name = sensorIntegerName.text.toString()
                    model.updateValue(sensor)
                    sensorIntegerName.setSelection(sensorIntegerName.text.length)
                }
            }
        }
    }

    private fun refreshUI(old: IntegerSensor, new: IntegerSensor) {
        if (old.name != new.name) {
            sensorIntegerName?.setText(new.name)
        }
        if (old.value != new.value) {
            sensorIntegerValue?.setText(new.value.toString())
            setText(new.deviceType, new.value)
        }
    }

    private fun setImage(type: DeviceType) {
        sensorIntegerImage?.setImageResource(
            when (type) {
                DeviceType.MOISTURE_SENSOR -> R.drawable.ic_moisture
                DeviceType.WATER_LEVEL_SENSOR -> R.drawable.ic_water_level
                DeviceType.HUMIDITY_SENSOR -> R.drawable.ic_humidity
                DeviceType.CELSIUS_SENSOR -> R.drawable.ic_celsius
                else -> R.drawable.sensor_icon
            }
        )
    }

    private fun setText(type: DeviceType, value: Int) {
        sensorIntegerValue?.setText(
            when (type) {
                DeviceType.MOISTURE_SENSOR,
                DeviceType.WATER_LEVEL_SENSOR,
                DeviceType.HUMIDITY_SENSOR -> "$value%"
                DeviceType.CELSIUS_SENSOR -> getString(R.string.degrees_C_suffix, value)
                else -> value.toString()
            }
        )
    }



}