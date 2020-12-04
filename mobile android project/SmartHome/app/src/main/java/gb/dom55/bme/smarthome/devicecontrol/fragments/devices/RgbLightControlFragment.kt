package gb.dom55.bme.smarthome.devicecontrol.fragments.devices

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import gb.dom55.bme.smarthome.model.devices.RgbLight
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.model.devices.AbstractDevice
import kotlinx.android.synthetic.main.colorpicker_whitebutton.*
import kotlinx.android.synthetic.main.fragment_light.*

class RgbLightControlFragment : DeviceControlFragmentBase() {

    private var rgbLight : RgbLight? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.fragment_light, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.deviceData.observe(viewLifecycleOwner, Observer<AbstractDevice> {
            val newLight = it as RgbLight
            val oldLight = rgbLight?: RgbLight()
            rgbLight = newLight
            if (firstRun) {
                firstRun = false
                setupUI()
            }
            refreshUI(oldLight, newLight)
        })
    }



    private fun setupUI() {
        rgbLight?.let { rgbLight ->

            colourPicker?.subscribe { color, _, _ ->
                rgbLight.red = Color.red(color)
                rgbLight.green = Color.green(color)
                rgbLight.blue = Color.blue(color)
                model.updateValue(rgbLight)
            }


            whiteSwitch?.setOnCheckedChangeListener { _, _ ->
                rgbLight.white = !rgbLight.white
                model.updateValue(rgbLight)

            }

            lightSwitch.setOnClickListener {
                rgbLight.on = !rgbLight.on
                model.updateValue(rgbLight)
            }

            brightnessBar.progress = rgbLight.brightness.toInt()

            brightnessBar.setOnProgressChangeListener { progressValue ->
                rgbLight.brightness = progressValue.toDouble()
                model.updateValue(rgbLight)
            }

            lightName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {

                    rgbLight.name = lightName.text.toString()
                    model.updateValue(rgbLight)
                    lightName.setSelection(lightName.text.length)
                }
            }

            if (rgbLight.on) {
                lightSwitch?.frame = 105
                lightSwitch?.speed = 1.0f
            } else {
                lightSwitch?.frame = 44
                lightSwitch?.speed = -1.0f
            }
        }
    }

    private fun refreshUI(old: RgbLight, new: RgbLight) {
        if (!new.on && lightSwitch?.speed != -1.0f) {
            lightSwitch?.speed = -1.0f
            lightSwitch?.playAnimation()
        } else if (new.on && lightSwitch?.speed != 1.0f){
            lightSwitch?.speed = 1.0f
            lightSwitch?.playAnimation()
        }

        if (new.white != old.white) {
            if (!new.white) {
                whiteSwitch?.setBackgroundColor(Color.WHITE)
                whiteSwitch?.setTextColor(Color.BLACK)
                whiteSwitch?.text = getString(R.string.white)
            } else {
                whiteSwitch?.setBackgroundColor(Color.DKGRAY)
                whiteSwitch?.setTextColor(Color.WHITE)
                whiteSwitch?.text = getString(R.string.rgb)
            }
        }
        if (new.name != lightName?.text.toString()) { lightName?.setText(new.name) }
    }
}