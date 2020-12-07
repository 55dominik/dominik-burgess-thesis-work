package gb.dom55.bme.smarthome.devicecontrol.fragments.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.viewholders.devices.ThermostatViewHolder
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.model.devices.Thermostat
import kotlinx.android.synthetic.main.fragment_thermostat.*

class ThermostatControlFragment : DeviceControlFragmentBase() {

    private lateinit var thermostat : Thermostat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.fragment_thermostat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.deviceData.observe(viewLifecycleOwner, Observer<AbstractDevice> {
            val newData = it as Thermostat
            thermostat = newData
            if (firstRun) {
                firstRun = false
                setupUI()
            }
            refreshUI(newData)
        })
    }

    private fun setupUI() {
        temperatureBar?.progress = temperatureToSeekBarValue(thermostat.targetTemperature)
        targetTemperature?.text = getString(R.string.degrees_C_suffix, thermostat.targetTemperature)
        currentTemperature?.text = getString(R.string.degrees_C_suffix, thermostat.temperature)
        thermostatName?.setText(thermostat.name)

        temperatureBar?.setOnProgressChangeListener { progress ->
            thermostat.targetTemperature = seekBarValueToTemperature(progress)
            model.updateValue(thermostat)
        }

        thermostatName?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                thermostat.name = thermostatName.text.toString()
                model.updateValue(thermostat)
                thermostatName.setSelection(thermostatName.text.length)
            }
        }
    }

    private fun refreshUI(newThermostat: Thermostat) {
        targetTemperature?.text = getString(R.string.degrees_C_suffix, thermostat.targetTemperature)
        currentTemperature?.text = getString(R.string.degrees_C_suffix, thermostat.temperature)
        if (newThermostat.name != thermostatName?.text.toString()) { thermostatName?.setText(newThermostat.name) }
    }

    private fun seekBarValueToTemperature(seekBarValue: Int): Int {
        return ((seekBarValue / 100.0) * (ThermostatViewHolder.MAX_TEMP - ThermostatViewHolder.MIN_TEMP) + ThermostatViewHolder.MIN_TEMP).toInt()
    }

    private fun temperatureToSeekBarValue(temperature: Int): Int {
        return (((temperature - ThermostatViewHolder.MIN_TEMP) / (ThermostatViewHolder.MAX_TEMP - ThermostatViewHolder.MIN_TEMP) * 100.0)).toInt()
    }
}