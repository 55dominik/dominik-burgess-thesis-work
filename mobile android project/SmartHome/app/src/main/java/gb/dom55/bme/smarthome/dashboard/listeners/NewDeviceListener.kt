package gb.dom55.bme.smarthome.dashboard.listeners

import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DataSnapshot
import gb.dom55.bme.smarthome.model.devices.AbstractDevice

interface NewDeviceListener {
    fun onDialogPositiveClick(dialog: DialogFragment, name: String, factory: AbstractDevice)
}