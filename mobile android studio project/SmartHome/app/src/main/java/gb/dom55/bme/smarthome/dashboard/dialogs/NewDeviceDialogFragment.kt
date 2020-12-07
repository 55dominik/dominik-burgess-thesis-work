package gb.dom55.bme.smarthome.dashboard.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import gb.dom55.bme.smarthome.model.DeviceType
import gb.dom55.bme.smarthome.model.devices.*
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.listeners.NewDeviceListener
import gb.dom55.bme.smarthome.model.AbstractDevice
import kotlinx.android.synthetic.main.fragment_dialog_newdevice.view.*
import java.lang.IllegalStateException

class NewDeviceDialogFragment(var listener: NewDeviceListener) : DialogFragment() {

    private lateinit var nameEditText : EditText
    private lateinit var typeSpinner: Spinner
    private var deviceFactory: AbstractDevice = RgbLight()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)

            builder.setView(getContentView())
                    .setPositiveButton("Add") { _, _ ->
                        listener.onDialogPositiveClick(this, nameEditText.text.toString(), deviceFactory)
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        dismiss()
                    }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun getContentView(): View {
        val contentView = LayoutInflater.from(context).inflate(R.layout.fragment_dialog_newdevice, null)
        nameEditText = contentView.dialogNewDeviceName
        typeSpinner = contentView.dialogNewDeviceType

        val spinnerDeviceValues = DeviceType.values().filter {
            it.type < DeviceType.NULL_DEVICE.type
        }.map {
            getString(it.nameResource)
        }

        val typeSpinnerAdapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                spinnerDeviceValues
        )
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeSpinnerAdapter
        typeSpinner.setSelection(0)
        typeSpinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                deviceFactory = AbstractDevice.getDeviceFactoryFromPosition(position)
            }
        })
        return contentView
    }

}