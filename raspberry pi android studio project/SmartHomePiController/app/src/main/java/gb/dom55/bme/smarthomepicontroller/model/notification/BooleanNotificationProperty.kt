package gb.dom55.bme.smarthomepicontroller.model.notification

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class BooleanNotificationProperty(
        devicePropertyNodeName: String,
        var propertyName: String,
        var isActive: Boolean = false,
        var trueMessage: String = "",
        var falseMessage: String = "",
        var notifyWhenTrue: Boolean = false,
        var notifyWhenFalse: Boolean = false
) : NotificationPropertyBase(devicePropertyNodeName) {

    companion object {
        fun getFromFirebase(notifSnapshot: DataSnapshot): NotificationPropertyBase {
            return BooleanNotificationProperty(
                devicePropertyNodeName = notifSnapshot.child("devicePropertyNodeName")
                    .getValue(String::class.java)!!,
                propertyName = notifSnapshot.child("propertyName").getValue(String::class.java)!!,
                isActive = notifSnapshot.child("isActive").getValue(Boolean::class.java)!!,
                trueMessage = notifSnapshot.child("trueMessage").getValue(String::class.java)!!,
                falseMessage = notifSnapshot.child("falseMessage").getValue(String::class.java)!!,
                notifyWhenTrue = notifSnapshot.child("notifyWhenTrue")
                    .getValue(Boolean::class.java)!!,
                notifyWhenFalse = notifSnapshot.child("notifyWhenFalse")
                    .getValue(Boolean::class.java)!!
            )
        }
    }

}