package gb.dom55.bme.smarthome.model.devices

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import gb.dom55.bme.smarthome.model.notification.IntegerNotificationProperty
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.IntegerSensorFragment

@IgnoreExtraProperties
class IntegerSensor(uid: String = "",
                    deviceid: String = "",
                    name: String = "",
                    var deviceType: DeviceType = DeviceType.INT_SENSOR)
    :AbstractDevice(uid, deviceid, name){


    var value: Int = 0

    override fun createDevice(userId: String, deviceId: String, name: String): IntegerSensor {
        return IntegerSensor(userId, deviceId, name, deviceType)
    }

    override fun getType(): DeviceType {
        return deviceType
    }

    override fun getAssociatedFragment(): DeviceControlFragmentBase {
        return IntegerSensorFragment()
    }

    override fun hasStatusView() = deviceType.hasSensorView
    override fun hasDashboardView() = deviceType.hasDashView

    override fun getDataClassFB(): DeviceBaseFB {
        return IntSensorFB(
            uid,
            deviceid,
            name,
            deviceType,
            value
        )
    }

    override fun getDataFromSnapshot(snapshot: DataSnapshot): AbstractDevice {
        return if (snapshot.getValue(IntegerSensor::class.java) == null) {
            IntegerSensor()
        } else {
            snapshot.getValue(IntegerSensor::class.java)!!
        }
    }

    override fun getNotificationProperties(): MutableList<NotificationPropertyBase> {
        return mutableListOf(
                IntegerNotificationProperty(
                        devicePropertyNodeName = ::value.name,
                        propertyName = "Simple value notification",
                        isActive = false,
                        message = "Sensor was activated",
                        triggerCondition = "EQ",
                        triggerValue = 0
                )
        )
    }

    private inner class IntSensorFB(
            var uid: String = "",
            var deviceid: String = "",
            var name: String = "",
            var deviceType: DeviceType,
            var value: Int = 0
    ) : DeviceBaseFB()
}