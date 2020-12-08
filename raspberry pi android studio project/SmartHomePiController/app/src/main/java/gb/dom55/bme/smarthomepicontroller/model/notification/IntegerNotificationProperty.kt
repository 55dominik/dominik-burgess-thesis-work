package gb.dom55.bme.smarthomepicontroller.model.notification

import com.google.firebase.database.DataSnapshot

class IntegerNotificationProperty(
        devicePropertyNodeName: String,
        var propertyName: String,
        var isActive: Boolean = false,
        var message: String = "",
        var triggerCondition: String = "",
        var triggerValue: Int = 0
) : NotificationPropertyBase(devicePropertyNodeName) {

    companion object {
        fun getFromFirebase(notifSnapshot: DataSnapshot): NotificationPropertyBase {
            return IntegerNotificationProperty(
                devicePropertyNodeName = notifSnapshot.child("devicePropertyNodeName")
                    .getValue(String::class.java)!!,
                propertyName = notifSnapshot.child("propertyName").getValue(String::class.java)!!,
                isActive = notifSnapshot.child("isActive").getValue(Boolean::class.java)!!,
                message = notifSnapshot.child("message").getValue(String::class.java)!!,
                triggerCondition = notifSnapshot.child("triggerCondition")
                    .getValue(String::class.java)!!,
                triggerValue = notifSnapshot.child("triggerValue").getValue(Int::class.java)!!
            )
        }
    }
}
