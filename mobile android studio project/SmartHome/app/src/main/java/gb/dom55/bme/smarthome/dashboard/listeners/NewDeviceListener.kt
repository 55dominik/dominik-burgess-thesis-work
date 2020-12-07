package gb.dom55.bme.smarthome.dashboard.listeners

import androidx.fragment.app.DialogFragment
import gb.dom55.bme.smarthome.model.AbstractDevice

interface NewDeviceListener {
    fun onDialogPositiveClick(dialog: DialogFragment, name: String, factory: AbstractDevice)
}