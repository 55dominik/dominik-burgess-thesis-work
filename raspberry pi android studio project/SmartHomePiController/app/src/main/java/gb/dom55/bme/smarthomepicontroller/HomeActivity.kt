package gb.dom55.bme.smarthomepicontroller

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeActivity : Activity(), LoggedInListener {

    private val TAG = "HomeActivity"

    private lateinit var uid: String
    private lateinit var userDevicesRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        LoginManager(this, this)

    }

    override fun loginSuccess() {
        uid = FirebaseAuth.getInstance().currentUser?.uid!!
        listenForDevicesChanges()
        GlobalScope.launch(Dispatchers.IO) {
            DeviceConnectionManager.startUdpIpExchange()
        }
        GlobalScope.launch(Dispatchers.IO) {
            DeviceDataReceiver.startTcpServer()
        }
    }

    private fun listenForDevicesChanges() {
        userDevicesRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(uid)
            .child("devices")
        userDevicesRef.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                p0.toException().printStackTrace()
            }

            override fun onChildMoved(p0: DataSnapshot, previousChildName: String?) {
                //not used
            }

            override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                // Possible change: friendly connection ID is added/changed
                DeviceManager.addDeviceFromUserNode(snapshot)
            }

            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                //get device, add value event listener
                DeviceManager.addDeviceFromUserNode(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //remove device and it's value event listener
                DeviceManager.removeDevice(snapshot)
            }

        })
    }

}
