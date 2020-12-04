package gb.dom55.bme.smarthome.model.devices

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase
import java.io.Serializable

@IgnoreExtraProperties
abstract class AbstractDevice(var uid: String = "",
                              var deviceid: String = "",
                              var name: String = "") : Serializable {
    abstract fun createDevice(userId: String, deviceId: String, name: String) : AbstractDevice
    abstract fun getType() : DeviceType
    abstract fun getAssociatedFragment() : DeviceControlFragmentBase
    abstract fun hasStatusView(): Boolean
    abstract fun hasDashboardView(): Boolean

    abstract fun getDataClassFB() : DeviceBaseFB
    abstract fun getDataFromSnapshot(snapshot: DataSnapshot) : AbstractDevice
    abstract fun getNotificationProperties() : MutableList<NotificationPropertyBase>
}