package gb.dom55.bme.smarthome.model.devices

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import gb.dom55.bme.smarthome.model.notification.BooleanNotificationProperty
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.BooleanSensorFragment
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.model.FirebaseDevice
import gb.dom55.bme.smarthome.model.DeviceType

@IgnoreExtraProperties
class BooleanSensor(uid: String = "",
                    deviceid: String = "",
                    name: String = "",
                    var deviceType: DeviceType = DeviceType.BOOL_SENSOR)
    : AbstractDevice(uid, deviceid, name){


    var isActive: Boolean = true



    override fun createDevice(userId: String, deviceId: String, name: String): BooleanSensor {
        return BooleanSensor(userId, deviceId, name, deviceType)
    }

    override fun getType(): DeviceType {
        return deviceType
    }

    override fun getAssociatedFragment(): DeviceControlFragmentBase {
        return BooleanSensorFragment()
    }

    override fun hasStatusView(): Boolean {
        return true
    }

    override fun hasDashboardView(): Boolean {
        return false
    }

    override fun getDataClassFB(): FirebaseDevice {
        return FirebaseBooleanSensor(uid, deviceid, name, isActive, deviceType)
    }

    override fun getDataFromSnapshot(snapshot: DataSnapshot): AbstractDevice {


        return if (snapshot.getValue(BooleanSensor::class.java) == null) {
            BooleanSensor()
        } else {
            snapshot.getValue(BooleanSensor::class.java)!!
        }
    }

    override fun getNotificationProperties(): MutableList<NotificationPropertyBase> {
        return mutableListOf(
                BooleanNotificationProperty(
                        devicePropertyNodeName = ::isActive.name,
                        propertyName = "Sensor notification",
                        trueMessage = "Sensor activated",
                        falseMessage = "Sensor deactivated",
                        isActive = false,
                        notifyWhenTrue = true,
                        notifyWhenFalse = false
                )
        )
    }

    //this class is required to write to firebase, read can be done into BooleanSensor()
    @IgnoreExtraProperties
    private inner class FirebaseBooleanSensor(
            var uid: String = "",
            var deviceid: String = "",
            var name: String = "",
            var isActive: Boolean = false,
            var deviceType: DeviceType
    ) : FirebaseDevice()

}