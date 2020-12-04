package gb.dom55.bme.smarthome.devicecontrol.fragments.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gb.dom55.bme.smarthome.R

class PlaceholderDeviceFragment : DeviceControlFragmentBase() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_not_implemented, container, false)
    }
}