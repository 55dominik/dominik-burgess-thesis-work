package gb.dom55.bme.smarthome.dashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.dashboard.viewholders.sensors.BoolSensorViewHolder
import gb.dom55.bme.smarthome.dashboard.viewholders.sensors.IntSensorViewHolder
import gb.dom55.bme.smarthome.model.devices.DeviceType
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.viewholders.devices.BaseViewHolder
import gb.dom55.bme.smarthome.dashboard.adapters.touchhelpers.RecyclerViewTouchHelperInterface
import gb.dom55.bme.smarthome.dashboard.viewholders.sensors.CoffeeSensorViewHolder
import gb.dom55.bme.smarthome.dashboard.viewholders.sensors.ThermostatStatusViewHolder
import gb.dom55.bme.smarthome.model.devices.AbstractDevice

class StatusAdapter(val context: Context) : RecyclerView.Adapter<BaseViewHolder>(),
        RecyclerViewTouchHelperInterface {

    private var devices = mutableListOf<AbstractDevice>()

    var clickListener: DeviceItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        return when(viewType) {
            DeviceType.BOOL_SENSOR.type,
            DeviceType.WATER_PRESENCE_SENSOR.type,
            DeviceType.MOTION_SENSOR.type,
            DeviceType.DOOR_SENSOR.type -> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_sensor_bool, parent, false)
                BoolSensorViewHolder(view, clickListener)
            }
            DeviceType.INT_SENSOR.type,
            DeviceType.CELSIUS_SENSOR.type,
            DeviceType.HUMIDITY_SENSOR.type,
            DeviceType.WATER_LEVEL_SENSOR.type,
            DeviceType.MOISTURE_SENSOR.type -> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_sensor_integer, parent, false)
                IntSensorViewHolder(view, clickListener)
            }
            DeviceType.COFFEE.type -> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_sensor_coffee, parent, false)
                CoffeeSensorViewHolder(view, clickListener)
            }
            DeviceType.THERMOSTAT.type -> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_sensor_thermostat, parent, false)
                return ThermostatStatusViewHolder(view, clickListener)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.placeholder_sensor_item, parent, false)
                PlaceholderViewHolder(view)
            }
        }

    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.setState(devices[position].deviceid)
    }

    override fun getItemViewType(position: Int): Int {
        return devices[position].getType().type
    }

    private fun addDevice(newDevice: AbstractDevice) {
        if (devices.any { it.deviceid == newDevice.deviceid }) return
        val position = devices.size
        devices.add(position, newDevice)
        notifyItemInserted(position)
    }

    fun addMultipleDevices(newDevices: List<AbstractDevice>) {
        val position = devices.size
        devices.addAll(position, newDevices)
        notifyItemRangeInserted(position, devices.size)
    }

    fun replaceDevices(newDevices: MutableList<AbstractDevice>) {
        for (device in newDevices) {
            if (!devices.any { it.deviceid == device.deviceid }) {
                addDevice(device)
            }
        }
    }

    fun removeDevice(device: AbstractDevice) {
        if (!devices.contains(device)) return
        val position = devices.indexOf(device)
        devices.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onItemDismissed(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.delete))
                .setMessage(context.getString(R.string.are_you_sure_you_want_to_delete))
                .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                    val tmp = devices.removeAt(position)
                    clickListener?.onDeviceDeleted(tmp)
                    notifyItemRemoved(position)
                }
                .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                    notifyDataSetChanged()
                }
        builder.create().show()
    }

    inner class PlaceholderViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.statusTestImage)
        var text: TextView = itemView.findViewById(R.id.statusTestText)
        override fun setState(deviceId: String) {}
    }

}