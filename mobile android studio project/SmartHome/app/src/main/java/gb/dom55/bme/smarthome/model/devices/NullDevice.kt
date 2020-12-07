package gb.dom55.bme.smarthome.model.devices

import com.google.firebase.database.DataSnapshot
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.PlaceholderDeviceFragment
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.model.FirebaseDevice
import gb.dom55.bme.smarthome.model.DeviceType

class NullDevice : AbstractDevice() {
    override fun createDevice(userId: String, deviceId: String, name: String): AbstractDevice {
        return this
    }

    override fun getType(): DeviceType {
        return DeviceType.NULL_DEVICE
    }

    override fun getAssociatedFragment(): DeviceControlFragmentBase {
        return PlaceholderDeviceFragment()
    }

    override fun hasStatusView(): Boolean {
        return false
    }

    override fun hasDashboardView(): Boolean {
        return false
    }

    override fun getDataClassFB(): FirebaseDevice {
        return FirebaseDevice()
    }

    override fun getDataFromSnapshot(snapshot: DataSnapshot): AbstractDevice {
        return NullDevice()
    }

    override fun getNotificationProperties(): MutableList<NotificationPropertyBase> {
        return mutableListOf()
    }

}