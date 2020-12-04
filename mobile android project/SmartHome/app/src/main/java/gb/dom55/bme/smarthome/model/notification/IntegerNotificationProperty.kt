package gb.dom55.bme.smarthome.model.notification

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class IntegerNotificationProperty(
        devicePropertyNodeName: String,
        var propertyName: String,
        var isActive: Boolean = false,
        var message: String = "",
        var triggerCondition: String = "",
        var triggerValue: Int = 0
) : NotificationPropertyBase(devicePropertyNodeName) {
    companion object {
        fun getFirebaseData(notifSnapshot: DataSnapshot): NotificationPropertyBase {
            return IntegerNotificationProperty(
                    devicePropertyNodeName = notifSnapshot.child("devicePropertyNodeName").getValue(String::class.java)!!,
                    propertyName = notifSnapshot.child("propertyName").getValue(String::class.java)!!,
                    isActive = notifSnapshot.child("isActive").getValue(Boolean::class.java)!!,
                    message = notifSnapshot.child("message").getValue(String::class.java)!!,
                    triggerCondition = notifSnapshot.child("triggerCondition").getValue(String::class.java)!!,
                    triggerValue = notifSnapshot.child("triggerValue").getValue(Int::class.java)!!
            )
        }

        const val viewType = 1
    }

    override fun updateFirebaseRecord(deviceId: String, position: Int) {
        val notifRef = FirebaseDatabase.getInstance().reference
                .child("notifications").child(deviceId).child(position.toString())
        notifRef.child("propertyName").setValue(propertyName)
        notifRef.child("isActive").setValue(isActive)
        notifRef.child("message").setValue(message)
        notifRef.child("triggerCondition").setValue(triggerCondition)
        notifRef.child("triggerValue").setValue(triggerValue)
        notifRef.child("type").setValue("integer")
        notifRef.child("devicePropertyNodeName").setValue(devicePropertyNodeName)
    }

    override fun getItemViewType(): Int {
        return viewType
    }


}