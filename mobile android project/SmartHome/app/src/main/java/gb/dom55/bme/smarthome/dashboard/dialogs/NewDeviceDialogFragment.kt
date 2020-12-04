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
import gb.dom55.bme.smarthome.model.devices.DeviceType
import gb.dom55.bme.smarthome.model.devices.*
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.listeners.NewDeviceListener
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

        val typeSpinnerAdapter = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                arrayListOf(
                        "Thermostat",
                        "Coffee",
                        "Blinds",
                        "RGB Light",
                        "Light",
                        "Kettle",
                        "Socket",
                        "Cooler",
                        "Dimmable Light",
                        "Boolean sensor",
                        "Motion sensor",
                        "Door sensor",
                        "Door lock",
                        "Water presence sensor",
                        "Water level sensor",
                        "Moisture sensor",
                        "Temperature (°C)",
                        "Humidity sensor",
                        "Integer sensor",
                        "IR remote",
                        "RGB remote"))
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeSpinnerAdapter
        typeSpinner.setSelection(0)
        typeSpinner.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                deviceFactory = when(parent?.getItemAtPosition(position)) {
                    "Thermostat" -> Thermostat()
                    "Coffee" -> CoffeeMaker()
                    "Blinds" -> DimmableLight(deviceType = DeviceType.BLINDS)
                    "RGB Light" -> RgbLight()
                    "Light" -> Light()
                    "Kettle" -> Light(deviceType = DeviceType.KETTLE)
                    "Socket" -> Light(deviceType = DeviceType.SOCKET)
                    "Cooler" -> Light(deviceType = DeviceType.AIR_CON)
                    "Door lock" -> Light(deviceType = DeviceType.DOOR_LOCK)
                    "Dimmable Light" -> DimmableLight()
                    "Boolean sensor" -> BooleanSensor()
                    "Door sensor" -> BooleanSensor(deviceType = DeviceType.DOOR_SENSOR)
                    "Motion sensor" -> BooleanSensor(deviceType = DeviceType.MOTION_SENSOR)
                    "Water presence sensor" -> BooleanSensor(deviceType = DeviceType.WATER_PRESENCE_SENSOR)
                    "Water level sensor" -> IntegerSensor(deviceType = DeviceType.WATER_LEVEL_SENSOR)
                    "Moisture sensor" -> IntegerSensor(deviceType = DeviceType.MOISTURE_SENSOR)
                    "Temperature (°C)" -> IntegerSensor(deviceType = DeviceType.CELSIUS_SENSOR)
                    "Humidity sensor" -> IntegerSensor(deviceType = DeviceType.HUMIDITY_SENSOR)
                    "Integer sensor" -> IntegerSensor()
                    "IR remote" -> IrRemote(deviceType = DeviceType.IR_REMOTE)
                    "RGB remote" -> IrRemote(deviceType = DeviceType.RGB_IR_REMOTE)
                    else -> RgbLight()
                }
            }

        })
        return contentView
    }

}