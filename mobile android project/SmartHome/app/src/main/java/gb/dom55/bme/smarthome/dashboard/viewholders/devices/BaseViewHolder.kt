package gb.dom55.bme.smarthome.dashboard.viewholders.devices

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun setState(deviceId: String)
}