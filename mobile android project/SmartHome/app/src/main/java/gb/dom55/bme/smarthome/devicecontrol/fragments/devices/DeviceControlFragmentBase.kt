package gb.dom55.bme.smarthome.devicecontrol.fragments.devices

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import gb.dom55.bme.smarthome.devicecontrol.DeviceControlViewModel

abstract class DeviceControlFragmentBase : Fragment() {
    protected var firstRun = true

    val model: DeviceControlViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
        model.forceUpdate()
    }

    override fun onPause() {
        firstRun = true
        super.onPause()
    }
}