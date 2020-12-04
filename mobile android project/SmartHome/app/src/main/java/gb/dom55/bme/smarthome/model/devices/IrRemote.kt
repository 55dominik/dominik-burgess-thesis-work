package gb.dom55.bme.smarthome.model.devices

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.DeviceControlFragmentBase
import gb.dom55.bme.smarthome.devicecontrol.fragments.devices.IrRemoteControlFragment

@IgnoreExtraProperties
class IrRemote(
        uid: String = "",
        deviceid: String = "",
        name: String = "",
        var deviceType: DeviceType = DeviceType.IR_REMOTE)
    : AbstractDevice(uid, deviceid, name) {

    var buttonMapping: MutableMap<String, String>
    var codeMapping: MutableMap<String, String>
    var currentButtonCode: String = ""

    init {
        when (deviceType) {
            DeviceType.RGB_IR_REMOTE -> {
                buttonMapping = mutableMapOf(
                        Pair("A1", "Power"),
                        Pair("A2", "1"),
                        Pair("A3", "4"),
                        Pair("A4", "7"),
                        Pair("A5", "+"),

                        Pair("B1", "<--"),
                        Pair("B2", "2"),
                        Pair("B3", "5"),
                        Pair("B4", "8"),
                        Pair("B5", "0"),

                        Pair("C1", "-->"),
                        Pair("C2", "3"),
                        Pair("C3", "6"),
                        Pair("C4", "9"),
                        Pair("C5", "#"),

                        Pair("D1", "⬆"),//up
                        Pair("D2", "⬇"),//down
                        Pair("D3", "\uD83D\uDD0A"),//vol up
                        Pair("D4", "\uD83D\uDD09"),//vol down
                        Pair("D5", "\uD83D\uDD07")//mute
                )

                codeMapping = mutableMapOf(
                        Pair("A1", "Power"),
                        Pair("A2", "1"),
                        Pair("A3", "4"),
                        Pair("A4", "7"),
                        Pair("A5", "+"),

                        Pair("B1", "<--"),
                        Pair("B2", "2"),
                        Pair("B3", "5"),
                        Pair("B4", "8"),
                        Pair("B5", "0"),

                        Pair("C1", "-->"),
                        Pair("C2", "3"),
                        Pair("C3", "6"),
                        Pair("C4", "9"),
                        Pair("C5", "#"),

                        Pair("D1", "⬆"),//up
                        Pair("D2", "⬇"),//down
                        Pair("D3", "\uD83D\uDD0A"),//vol up
                        Pair("D4", "\uD83D\uDD09"),//vol down
                        Pair("D5", "\uD83D\uDD07")//mute
                )
            }
            DeviceType.IR_REMOTE -> {
                buttonMapping = mutableMapOf(
                        Pair("A1", "Power"),
                        Pair("A2", "1"),
                        Pair("A3", "4"),
                        Pair("A4", "7"),
                        Pair("A5", "+"),

                        Pair("B1", "<--"),
                        Pair("B2", "2"),
                        Pair("B3", "5"),
                        Pair("B4", "8"),
                        Pair("B5", "0"),

                        Pair("C1", "-->"),
                        Pair("C2", "3"),
                        Pair("C3", "6"),
                        Pair("C4", "9"),
                        Pair("C5", "#"),

                        Pair("D1", "⬆"),//up
                        Pair("D2", "⬇"),//down
                        Pair("D3", "\uD83D\uDD0A"),//vol up
                        Pair("D4", "\uD83D\uDD09"),//vol down
                        Pair("D5", "\uD83D\uDD07")//mute
                )

                codeMapping = mutableMapOf(
                        Pair("A1", "Power"),
                        Pair("A2", "1"),
                        Pair("A3", "4"),
                        Pair("A4", "7"),
                        Pair("A5", "+"),

                        Pair("B1", "<--"),
                        Pair("B2", "2"),
                        Pair("B3", "5"),
                        Pair("B4", "8"),
                        Pair("B5", "0"),

                        Pair("C1", "-->"),
                        Pair("C2", "3"),
                        Pair("C3", "6"),
                        Pair("C4", "9"),
                        Pair("C5", "#"),

                        Pair("D1", "⬆"),//up
                        Pair("D2", "⬇"),//down
                        Pair("D3", "\uD83D\uDD0A"),//vol up
                        Pair("D4", "\uD83D\uDD09"),//vol down
                        Pair("D5", "\uD83D\uDD07")//mute
                )
            }
            else -> {
                buttonMapping = hashMapOf()
                codeMapping = hashMapOf()
            }
        }
    }

    override fun createDevice(userId: String, deviceId: String, name: String): IrRemote {
        return IrRemote(userId, deviceId, name, deviceType)
    }

    override fun getType(): DeviceType = deviceType

    override fun getAssociatedFragment(): DeviceControlFragmentBase {
        return IrRemoteControlFragment()
    }

    override fun hasStatusView(): Boolean {
        return false
    }

    override fun hasDashboardView(): Boolean {
        return true
    }

    override fun getDataClassFB(): DeviceBaseFB {
        return IRremoteFB(uid, deviceid, name, currentButtonCode, buttonMapping, codeMapping, deviceType)
    }

    override fun getDataFromSnapshot(snapshot: DataSnapshot): AbstractDevice {
        return snapshot.getValue(IrRemote::class.java)!!
    }

    override fun getNotificationProperties(): MutableList<NotificationPropertyBase> {
        return mutableListOf()
    }

    @IgnoreExtraProperties
    private inner class IRremoteFB(
            var uid: String = "",
            var deviceid: String = "",
            var name: String = "",
            var currentButton : String = "",
            var buttonMapping: MutableMap<String, String> = mutableMapOf(),
            var codeMapping: MutableMap<String, String> = mutableMapOf(),
            var deviceType: DeviceType
    ) : DeviceBaseFB()
}