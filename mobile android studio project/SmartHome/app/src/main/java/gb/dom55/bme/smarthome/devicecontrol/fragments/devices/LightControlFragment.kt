package gb.dom55.bme.smarthome.devicecontrol.fragments.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import gb.dom55.bme.smarthome.model.DeviceType
import gb.dom55.bme.smarthome.model.devices.Light
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.model.AbstractDevice
import kotlinx.android.synthetic.main.colorpicker_whitebutton.*
import kotlinx.android.synthetic.main.fragment_light.*

class LightControlFragment : DeviceControlFragmentBase() {

    private lateinit var light : Light


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.fragment_light, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.deviceData.observe(viewLifecycleOwner, Observer<AbstractDevice> {
            val newLight = it as Light
            light = newLight
            if (firstRun) {
                firstRun = false
                setupUI()
            }
            refreshUI(newLight)
        })
    }

    private fun setupUI() {

        brightnessBar.visibility = View.INVISIBLE
        colourPicker.visibility = View.INVISIBLE
        whiteSwitch.visibility = View.INVISIBLE

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

        setImage(light)
        if (light.deviceType == DeviceType.LIGHT) {
            if (light.on) {
                lightSwitch?.frame = 105
                lightSwitch?.speed = 1.0f
            } else {
                lightSwitch?.frame = 44
                lightSwitch?.speed = -1.0f
            }
        }

    }

    private fun refreshUI(new: Light) {
        setImage(new)
        if (new.name != lightName?.text.toString()) { lightName?.setText(new.name) }
    }

    private fun setImage(deviceData: Light) {
        when (deviceData.deviceType) {
            DeviceType.SOCKET -> {
                lightSwitch?.setImageResource(if (deviceData.on) {
                    R.drawable.ic_socket_on
                } else {
                    R.drawable.ic_socket_off
                })
            }
            DeviceType.KETTLE -> {
                lightSwitch?.setImageResource(R.drawable.ic_kettle)
            }
            DeviceType.AIR_CON -> {
                lightSwitch?.setImageResource(if (deviceData.on) {
                    R.drawable.ic_aircon_on
                } else {
                    R.drawable.ic_aircon_off
                })
            }
            DeviceType.DOOR_LOCK -> {
                lightSwitch?.setImageResource(if (deviceData.on) {
                    R.drawable.ic_door_locked
                } else {
                    R.drawable.ic_door_unlocked
                })
            }
            else -> {
                if (!deviceData.on && lightSwitch?.speed != -1.0f) {
                    lightSwitch?.speed = -1.0f
                    lightSwitch?.playAnimation()
                } else if (deviceData.on && lightSwitch?.speed != 1.0f){
                    lightSwitch?.speed = 1.0f
                    lightSwitch?.playAnimation()
                }
            }
        }
    }


}