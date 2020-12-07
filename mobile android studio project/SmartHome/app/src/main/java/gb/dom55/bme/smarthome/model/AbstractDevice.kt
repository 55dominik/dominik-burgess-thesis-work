package gb.dom55.bme.smarthome.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase
import gb.dom55.bme.smarthome.model.devices.*
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

    abstract fun getDataClassFB() : FirebaseDevice
    abstract fun getDataFromSnapshot(snapshot: DataSnapshot) : AbstractDevice
    abstract fun getNotificationProperties() : MutableList<NotificationPropertyBase>

    companion object {
        fun getDeviceFactoryFromPosition(position: Int) : AbstractDevice {
            val deviceType = DeviceType.values()[position]
            return getDeviceFromType(deviceType)
        }

        // Always use this function when a device class is needed from a DeviceType
        // Never copy this elsewhere, this must be the single point where this choice happens
        fun getDeviceFromType(deviceType: DeviceType, uid: String = "", deviceId: String = "") : AbstractDevice {
            return when (deviceType) {
                DeviceType.RGBLIGHT -> RgbLight(uid, deviceId)
                DeviceType.LIGHT -> Light(uid, deviceId, deviceType = deviceType)
                DeviceType.DIMMABLE_LIGHT -> DimmableLight(uid, deviceId, deviceType = deviceType)
                DeviceType.BOOL_SENSOR -> BooleanSensor(uid, deviceId, deviceType = deviceType)
                DeviceType.INT_SENSOR -> IntegerSensor(uid, deviceId, deviceType = deviceType)
                DeviceType.KETTLE -> Light(uid, deviceId, deviceType = deviceType)
                DeviceType.SOCKET -> Light(uid, deviceId, deviceType = deviceType)
                DeviceType.AIR_CON -> Light(uid, deviceId, deviceType = deviceType)
                DeviceType.DOOR_LOCK -> Light(uid, deviceId, deviceType = deviceType)
                DeviceType.IR_REMOTE -> IrRemote(uid, deviceId, deviceType = deviceType)
                DeviceType.RGB_IR_REMOTE -> IrRemote(uid, deviceId, deviceType = deviceType)
                DeviceType.TV_REMOTE -> IrRemote(uid, deviceId, deviceType = DeviceType.IR_REMOTE)
                DeviceType.COFFEE -> CoffeeMaker(uid, deviceId, deviceType = deviceType)
                DeviceType.BLINDS -> DimmableLight(uid, deviceId, deviceType = deviceType)
                DeviceType.RADIATOR -> DimmableLight(uid, deviceId, deviceType = DeviceType.AIR_CON)
                DeviceType.DOOR_SENSOR -> BooleanSensor(uid, deviceId, deviceType = deviceType)
                DeviceType.MOTION_SENSOR -> BooleanSensor(uid, deviceId, deviceType = deviceType)
                DeviceType.WATER_PRESENCE_SENSOR -> BooleanSensor(uid, deviceId, deviceType = deviceType)
                DeviceType.CELSIUS_SENSOR -> IntegerSensor(uid, deviceId, deviceType = deviceType)
                DeviceType.HUMIDITY_SENSOR -> IntegerSensor(uid, deviceId, deviceType = deviceType)
                DeviceType.WATER_LEVEL_SENSOR -> IntegerSensor(uid, deviceId, deviceType = deviceType)
                DeviceType.MOISTURE_SENSOR -> IntegerSensor(uid, deviceId, deviceType = deviceType)
                DeviceType.THERMOSTAT -> Thermostat(uid, deviceId, deviceType = deviceType)
                DeviceType.NULL_DEVICE -> NullDevice()
            }
        }
    }
}