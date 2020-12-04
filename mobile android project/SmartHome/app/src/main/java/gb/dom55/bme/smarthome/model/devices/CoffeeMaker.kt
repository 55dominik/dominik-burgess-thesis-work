package gb.dom55.bme.smarthome.model.devices

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import gb.dom55.bme.smarthome.model.notification.BooleanNotificationProperty
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.CoffeeMakerControlFragment
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase

class CoffeeMaker(uid: String = "",
                  deviceid: String = "",
                  name: String = "",
                  var deviceType: DeviceType = DeviceType.COFFEE)
    :AbstractDevice(uid, deviceid, name){

    var enoughWater: Boolean = true
    var cupPresent: Boolean = true
    var makeCoffee: Boolean = false

    override fun createDevice(userId: String, deviceId: String, name: String): AbstractDevice {
        return CoffeeMaker(userId, deviceId, name, deviceType)
    }

    override fun getType() = deviceType;

    override fun getAssociatedFragment(): DeviceControlFragmentBase {
        return CoffeeMakerControlFragment()
    }

    override fun hasStatusView() = true;
    override fun hasDashboardView() = true

    override fun getDataClassFB(): DeviceBaseFB {
        return CoffeeMakerFB(uid, deviceid, name, enoughWater, cupPresent, makeCoffee, deviceType)
    }

    override fun getDataFromSnapshot(snapshot: DataSnapshot): AbstractDevice {
        return snapshot.getValue(CoffeeMaker::class.java)!!
    }

    override fun getNotificationProperties(): MutableList<NotificationPropertyBase> {
        return mutableListOf(
            BooleanNotificationProperty(
                devicePropertyNodeName = ::enoughWater.name,
                propertyName = "Water is present",
                trueMessage = "Enough water",
                falseMessage = "Not enough water",
                isActive = true,
                notifyWhenTrue = false,
                notifyWhenFalse = true
            ),
            BooleanNotificationProperty(
                devicePropertyNodeName = ::cupPresent.name,
                propertyName = "Cup in machine",
                trueMessage = "Cup is present",
                falseMessage = "No cup detected",
                isActive = true,
                notifyWhenTrue = false,
                notifyWhenFalse = true
            )
        )
    }

    @IgnoreExtraProperties
    private inner class CoffeeMakerFB(
            var uid: String = "",
            var deviceid: String = "",
            var name: String = "",
            var enoughWater: Boolean = true,
            var cupPresent: Boolean = true,
            var makeCoffee: Boolean = false,
            var deviceType: DeviceType
    ) : DeviceBaseFB()
}