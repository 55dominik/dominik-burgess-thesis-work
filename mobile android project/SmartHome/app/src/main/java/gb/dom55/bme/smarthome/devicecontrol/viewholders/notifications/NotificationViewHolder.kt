package gb.dom55.bme.smarthome.devicecontrol.viewholders.notifications

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase

abstract class NotificationViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {
    abstract fun bindViewHolder(notifProperty: NotificationPropertyBase, index: Int, deviceId: String)
}