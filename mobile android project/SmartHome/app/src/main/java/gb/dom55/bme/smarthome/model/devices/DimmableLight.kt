package gb.dom55.bme.smarthome.model.devices

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DimmableLightControlFragment

@IgnoreExtraProperties
class DimmableLight(
        uid: String = "",
        deviceid: String = "",
        name: String = "",
        var deviceType: DeviceType = DeviceType.DIMMABLE_LIGHT)
    : AbstractDevice(uid, deviceid, name) {

    var on: Boolean = true
    var brightness: Double = 20.0

    override fun createDevice(userId: String, deviceId: String, name: String): DimmableLight {
        return DimmableLight(userId, deviceId, name, deviceType)
    }

    override fun getType(): DeviceType = deviceType

    override fun getAssociatedFragment(): DeviceControlFragmentBase {
        return DimmableLightControlFragment()
    }

    override fun hasStatusView(): Boolean {
        return false
    }

    override fun hasDashboardView(): Boolean {
        return true
    }

    override fun getDataClassFB(): DeviceBaseFB {
        return DimmableLightFB(uid, deviceid, name, on, brightness, deviceType)
    }

    override fun getDataFromSnapshot(snapshot: DataSnapshot): AbstractDevice {
        return snapshot.getValue(DimmableLight::class.java)!!
    }

    @IgnoreExtraProperties
    private inner class DimmableLightFB(
            var uid: String = "",
            var deviceid: String = "",
            var name: String = "",
            var on: Boolean = false,
            var brightness: Double = 0.0,
            var deviceType: DeviceType
    ) : DeviceBaseFB()

    override fun getNotificationProperties(): MutableList<NotificationPropertyBase> {
        return mutableListOf()
    }
}