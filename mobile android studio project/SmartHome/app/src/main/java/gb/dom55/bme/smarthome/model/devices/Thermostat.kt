package gb.dom55.bme.smarthome.model.devices

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.ThermostatControlFragment
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.model.FirebaseDevice
import gb.dom55.bme.smarthome.model.DeviceType
import gb.dom55.bme.smarthome.model.notification.IntegerNotificationProperty
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase

@IgnoreExtraProperties
class Thermostat(uid: String = "",
                 deviceid: String = "",
                 name: String = "",
                 var deviceType: DeviceType = DeviceType.THERMOSTAT)
    : AbstractDevice(uid, deviceid, name) {

    var temperature: Int = 0
    var targetTemperature: Int = 0

    override fun createDevice(userId: String, deviceId: String, name: String): AbstractDevice {
        return Thermostat(userId, deviceId, name, deviceType)
    }

    override fun getType(): DeviceType {
        return deviceType
    }

    override fun getAssociatedFragment(): DeviceControlFragmentBase {
        return ThermostatControlFragment()
    }

    override fun hasStatusView() = deviceType.hasSensorView
    override fun hasDashboardView() = deviceType.hasDashView

    override fun getDataClassFB(): FirebaseDevice {
        return FirebaseThermostat(
            uid,
            deviceid,
            name,
            deviceType,
            temperature,
            targetTemperature
        )
    }

    override fun getDataFromSnapshot(snapshot: DataSnapshot): AbstractDevice {
        return if (snapshot.getValue(Thermostat::class.java) == null) {
            Thermostat()
        } else {
            snapshot.getValue(Thermostat::class.java)!!
        }
    }

    override fun getNotificationProperties(): MutableList<NotificationPropertyBase> {
        return mutableListOf(
                IntegerNotificationProperty(
                        devicePropertyNodeName = ::temperature.name,
                        propertyName = "Temperature notification",
                        isActive = false,
                        message = "Temperature too low",
                        triggerCondition = "LT",
                        triggerValue = 5
                )
        )
    }

    private inner class FirebaseThermostat(
            var uid: String = "",
            var deviceid: String = "",
            var name: String = "",
            var deviceType: DeviceType,
            var temperature: Int = 0,
            var targetTemperature: Int = 0
    ) : FirebaseDevice()
}