package gb.dom55.bme.smarthome.devicecontrol.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.model.notification.BooleanNotificationProperty
import gb.dom55.bme.smarthome.model.notification.IntegerNotificationProperty
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.devicecontrol.viewholders.notifications.BooleanViewHolder
import gb.dom55.bme.smarthome.devicecontrol.viewholders.notifications.IntegerViewHolder
import gb.dom55.bme.smarthome.devicecontrol.viewholders.notifications.NotificationViewHolder

class NotificationsAdapter(val device: AbstractDevice, val context: Context) : RecyclerView.Adapter<NotificationViewHolder>() {

    private var notifications = mutableListOf<NotificationPropertyBase>()

    fun add(newNotifications: MutableList<NotificationPropertyBase>){
        notifications = newNotifications
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return when (viewType) {
            BooleanNotificationProperty.viewType -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notif_boolean, parent, false)
                BooleanViewHolder(view, context)
            }
            IntegerNotificationProperty.viewType -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notif_integer, parent, false)
                IntegerViewHolder(view, context)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notif_boolean, parent, false)
                BooleanViewHolder(view, context)
            }
        }

    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bindViewHolder(notifications[position], position, device.deviceid)
    }

    override fun getItemViewType(position: Int): Int {
        return notifications[position].getItemViewType()
    }

}