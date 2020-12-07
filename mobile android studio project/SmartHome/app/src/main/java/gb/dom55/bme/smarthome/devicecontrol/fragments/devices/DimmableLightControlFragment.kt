package gb.dom55.bme.smarthome.devicecontrol.fragments.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import gb.dom55.bme.smarthome.model.devices.DimmableLight
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.model.DeviceType
import kotlinx.android.synthetic.main.colorpicker_whitebutton.*
import kotlinx.android.synthetic.main.fragment_light.*

class DimmableLightControlFragment : DeviceControlFragmentBase() {

    private lateinit var light : DimmableLight


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.fragment_light, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.deviceData.observe(viewLifecycleOwner, Observer<AbstractDevice> {
            val newLight = it as DimmableLight
            light = newLight
            if (firstRun) {
                firstRun = false
                setupUI()
            }
            refreshUI(newLight)
        })

    }

    private fun setupUI() {
        brightnessBar.visibility = View.VISIBLE
        colourPicker.visibility = View.GONE
        whiteSwitch.visibility = View.GONE
        colourPickerContainer.visibility = View.GONE

        lightSwitch.setOnClickListener {
            light.on = !light.on
            model.updateValue(light)
        }

        lightName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {

                light.name = lightName.text.toString()
                model.updateValue(light)
                lightName.setSelection(lightName.text.length)
            }
        }

        brightnessBar.progress = light.brightness.toInt()

        brightnessBar.setOnProgressChangeListener { progressValue ->
            light.brightness = progressValue.toDouble()
            light.on = progressValue > 30
            model.updateValue(light)
        }
        if (light.deviceType == DeviceType.DIMMABLE_LIGHT)
        if (light.on) {
            lightSwitch?.frame = 105
            lightSwitch?.speed = 1.0f
        } else {
            lightSwitch?.frame = 44
            lightSwitch?.speed = -1.0f
        }

    }

    private fun refreshUI(new: DimmableLight) {
        setImage(new)
        if (new.name != lightName?.text.toString()) { lightName?.setText(new.name) }
    }

    private fun setImage(deviceData: DimmableLight) {
        when (deviceData.deviceType) {

            DeviceType.BLINDS -> {
                lightSwitch?.setImageResource(
                        if (deviceData.on) {
                            when (deviceData.brightness.toInt()) {
                                in 0..33 -> {
                                    R.drawable.ic_blinds_low
                                }
                                in 34..66 -> {
                                    R.drawable.ic_blinds_middle
                                }
                                else -> {
                                    R.drawable.ic_blinds_high
                                }
                            }
                        } else {
                            R.drawable.ic_blinds_low
                        }
                )
            }

            else -> {
                if (!deviceData.on) {
                    lightSwitch?.speed = -1.0f
                    lightSwitch?.playAnimation()
                } else {
                    lightSwitch?.speed = 1.0f
                    lightSwitch?.playAnimation()
                }
            }
        }
    }



}