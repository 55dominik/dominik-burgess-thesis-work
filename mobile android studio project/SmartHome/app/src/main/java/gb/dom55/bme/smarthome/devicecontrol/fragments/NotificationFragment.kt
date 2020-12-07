package gb.dom55.bme.smarthome.devicecontrol.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import gb.dom55.bme.smarthome.devicecontrol.adapters.NotificationsAdapter
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.model.notification.BooleanNotificationProperty
import gb.dom55.bme.smarthome.model.notification.IntegerNotificationProperty
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.devicecontrol.DeviceControlViewModel
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationFragment : Fragment() {

    val model: DeviceControlViewModel by activityViewModels()
    private lateinit var notificationsAdapter: NotificationsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.deviceData.observe(viewLifecycleOwner, Observer<AbstractDevice> {
            it?.let {
                notificationsAdapter = NotificationsAdapter(it, requireContext())
                notificationsRV.adapter = notificationsAdapter
                notificationsRV.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

                getNotificationsFromFirebase(it)
            }
        })
    }

    private fun getNotificationsFromFirebase(device: AbstractDevice) {
        val listOfNotifProps = mutableListOf<NotificationPropertyBase>()
        val dataRef = FirebaseDatabase.getInstance().reference
                .child("notifications").child(device.deviceid)

        dataRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    writeDeviceToFirebase(device)
                    return
                }
                for (notifSnapshot in snapshot.children) {
                    val type = notifSnapshot.child("type").getValue(String::class.java)
                    if (type == "boolean") {
                        listOfNotifProps.add(BooleanNotificationProperty.getFirebaseData(notifSnapshot))
                    }
                    if (type == "integer") {
                        listOfNotifProps.add(IntegerNotificationProperty.getFirebaseData(notifSnapshot))
                    }
                }
                notificationsAdapter.add(listOfNotifProps)
            }

            override fun onCancelled(p0: DatabaseError) {
                if (p0.code == -3) {
                    writeDeviceToFirebase(device)
                }

                p0.toException().printStackTrace()
            }

        })
    }

    private fun writeDeviceToFirebase(device: AbstractDevice) {
        val listOfNotifProps = device.getNotificationProperties()
        notificationsAdapter.add(listOfNotifProps)

        // IMPORTANT
        // user id needs to written before any other data can be accessed as per database rules
        FirebaseDatabase.getInstance().reference
                .child("notifications").child(device.deviceid)
                .child("userId").setValue(FirebaseAuth.getInstance().currentUser?.uid!!)
                .addOnSuccessListener {
                    for ((position, notifProp) in listOfNotifProps.withIndex()) {
                        notifProp.updateFirebaseRecord(device.deviceid, position)
                    }
                }

    }

}