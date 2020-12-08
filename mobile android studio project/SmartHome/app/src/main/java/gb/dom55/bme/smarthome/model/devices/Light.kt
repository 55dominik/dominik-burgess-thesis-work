package gb.dom55.bme.smarthome.model.devices

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.LightControlFragment
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.model.FirebaseDevice
import gb.dom55.bme.smarthome.model.DeviceType

@IgnoreExtraProperties
class Light(
        uid: String = "",
        deviceid: String = "",
        name: String = "",
        var deviceType: DeviceType = DeviceType.LIGHT)
    : AbstractDevice(uid, deviceid, name) {

    var on: Boolean = false

    override fun createDevice(userId: String, deviceId: String, name: String): Light {
        return Light(userId, deviceId, name, deviceType)
    }

    override fun getType(): DeviceType = deviceType

    override fun getAssociatedFragment(): DeviceControlFragmentBase {
        return LightControlFragment()
    }

    override fun hasStatusView(): Boolean {
        return false
    }

    override fun hasDashboardView(): Boolean {
        return true
    }

    override fun getDataClassFB(): FirebaseDevice {
        return FirebaseLight(uid, deviceid, name, on, deviceType)
    }

    override fun getDataFromSnapshot(snapshot: DataSnapshot): AbstractDevice {
        return snapshot.getValue(Light::class.java)!!
    }

    override fun getNotificationProperties(): MutableList<NotificationPropertyBase> {
        return mutableListOf()
    }

    @Suppress("unused")
    @IgnoreExtraProperties
    private inner class FirebaseLight(
            var uid: String = "",
            var deviceid: String = "",
            var name: String = "",
            var on: Boolean = false,
            var deviceType: DeviceType
    ) : FirebaseDevice()
}