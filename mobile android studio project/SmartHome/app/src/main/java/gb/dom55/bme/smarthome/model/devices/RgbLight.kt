package gb.dom55.bme.smarthome.model.devices

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.RgbLightControlFragment
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.model.FirebaseDevice
import gb.dom55.bme.smarthome.model.DeviceType

@IgnoreExtraProperties
class RgbLight(uid: String = "", deviceid: String = "", name: String = "")
    : AbstractDevice(uid, deviceid, name) {
    var on: Boolean = false
    var white: Boolean = true
    var brightness: Double = 99.0
    var red: Int = 234
    var green: Int = 234
    var blue: Int = 234

    override fun createDevice(userId: String, deviceId: String, name: String): RgbLight {
        return RgbLight(userId, deviceId, name)
    }

    override fun getType(): DeviceType = DeviceType.RGBLIGHT

    override fun getAssociatedFragment(): DeviceControlFragmentBase {
        return RgbLightControlFragment()
    }

    override fun hasStatusView(): Boolean {
        return false
    }

    override fun hasDashboardView(): Boolean {
        return true
    }

    override fun getDataClassFB(): FirebaseDevice {
        return FirebaseRgbLight(uid, deviceid, name, on, white, brightness, red, green, blue)
    }

    override fun getDataFromSnapshot(snapshot: DataSnapshot): AbstractDevice {
        return snapshot.getValue(RgbLight::class.java)!!
    }

    override fun getNotificationProperties(): MutableList<NotificationPropertyBase> {
        return mutableListOf()
    }

    @IgnoreExtraProperties
    private inner class FirebaseRgbLight(
            var uid: String,
            var deviceid: String,
            var name: String,

            var on: Boolean,
            var white: Boolean,
            var brightness: Double,
            var red: Int,
            var green: Int,
            var blue: Int
    ) : FirebaseDevice()


}