package gb.dom55.bme.smarthome.model.notification

abstract class NotificationPropertyBase(val devicePropertyNodeName: String) {
    abstract fun updateFirebaseRecord(deviceId: String, position: Int)
    abstract fun getItemViewType() : Int
}