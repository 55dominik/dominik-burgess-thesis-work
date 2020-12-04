package gb.dom55.bme.smarthome.dashboard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import gb.dom55.bme.smarthome.model.devices.DeviceType
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.adapters.touchhelpers.RecyclerViewTouchHelperInterface
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.dashboard.viewholders.devices.*
import gb.dom55.bme.smarthome.model.devices.AbstractDevice


class DevicesAdapter(val context: Context) : RecyclerView.Adapter<BaseViewHolder>(),
        RecyclerViewTouchHelperInterface {

    var clickListener: DeviceItemClickListener? = null

    private var devices = mutableListOf<AbstractDevice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {

        when(viewType) {
            DeviceType.LIGHT.type,
            DeviceType.KETTLE.type,
            DeviceType.SOCKET.type,
            DeviceType.AIR_CON.type,
            DeviceType.DOOR_LOCK.type -> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_dash_light, parent, false)
                return LightViewHolder(view, clickListener)
            }
            DeviceType.DIMMABLE_LIGHT.type,
            DeviceType.BLINDS.type-> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_dash_dimlight, parent, false)
                return DimmableLightViewHolder(view, clickListener)
            }
            DeviceType.RGBLIGHT.type -> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_dash_rgblight, parent, false)
                return RgbLightViewHolder(view, clickListener)
            }
            DeviceType.RGB_IR_REMOTE.type,
            DeviceType.IR_REMOTE.type -> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_dash_ir_remote, parent, false)
                return IrRemoteViewHolder(view, clickListener)
            }
            DeviceType.COFFEE.type -> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_dash_coffee, parent, false)
                return CoffeeMakerViewHolder(view, clickListener)
            }
            DeviceType.THERMOSTAT.type -> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.item_dash_thermostat, parent, false)
                return ThermostatViewHolder(view, clickListener)
            }
            else -> {
                val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.placeholder_device_item,parent, false)
                return PlaceholderViewHolder(view)
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

    private fun addDevice(newDevice: AbstractDevice) {
        if (devices.any { it.deviceid == newDevice.deviceid }) return
        val position = devices.size
        devices.add(position, newDevice)
        notifyItemInserted(position)
    }

    fun addMultipleDevices(newDevices: List<AbstractDevice>) {
        val position = devices.size
        devices.addAll(position, newDevices)
        notifyItemRangeInserted(position, newDevices.size)
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


    inner class PlaceholderViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var image: ImageView = itemView.findViewById(R.id.testImage)
        var switch: Switch = itemView.findViewById(R.id.testSwitch)

        override fun setState(deviceId: String) {
            switch.setText("Placeholder")
            switch.setOnCheckedChangeListener { _, isChecked ->
                when(isChecked) {
                    true -> image.setImageResource(R.drawable.bulbon)
                    else -> image.setImageResource(R.drawable.bulboff)
                }
            }
            when(switch.isChecked) {
                true -> image.setImageResource(R.drawable.bulbon)
                else -> image.setImageResource(R.drawable.bulboff)
            }
        }

    }

}