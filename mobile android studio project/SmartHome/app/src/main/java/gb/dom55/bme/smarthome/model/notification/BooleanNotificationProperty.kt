package gb.dom55.bme.smarthome.model.notification

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
        fun getFirebaseData(notifSnapshot: DataSnapshot): NotificationPropertyBase {
            return BooleanNotificationProperty(
                    devicePropertyNodeName = notifSnapshot.child("devicePropertyNodeName").getValue(String::class.java)!!,
                    propertyName = notifSnapshot.child("propertyName").getValue(String::class.java)!!,
                    isActive = notifSnapshot.child("isActive").getValue(Boolean::class.java)!!,
                    trueMessage = notifSnapshot.child("trueMessage").getValue(String::class.java)!!,
                    falseMessage = notifSnapshot.child("falseMessage").getValue(String::class.java)!!,
                    notifyWhenTrue = notifSnapshot.child("notifyWhenTrue").getValue(Boolean::class.java)!!,
                    notifyWhenFalse = notifSnapshot.child("notifyWhenFalse").getValue(Boolean::class.java)!!
            )
        }

        const val viewType = 0
    }

    override fun updateFirebaseRecord(deviceId: String, position: Int) {

        val notifRef = FirebaseDatabase.getInstance().reference
                .child("notifications").child(deviceId).child(position.toString())
        notifRef.child("propertyName").setValue(propertyName)
        notifRef.child("isActive").setValue(isActive)
        notifRef.child("trueMessage").setValue(trueMessage)
        notifRef.child("falseMessage").setValue(falseMessage)
        notifRef.child("notifyWhenTrue").setValue(notifyWhenTrue)
        notifRef.child("notifyWhenFalse").setValue(notifyWhenFalse)
        notifRef.child("type").setValue("boolean")
        notifRef.child("devicePropertyNodeName").setValue(devicePropertyNodeName)

    }

    override fun getItemViewType(): Int {
        return viewType
    }




}