package gb.dom55.bme.smarthome.devicecontrol.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.devicecontrol.DeviceControlViewModel
import kotlinx.android.synthetic.main.fragment_setup_content_frame.*
import org.hashids.Hashids

class SetupFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var userId: String
    val model: DeviceControlViewModel by activityViewModels()
    private var deviceid = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid!!

        return inflater.inflate(R.layout.fragment_setup_content_frame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deviceid = model.deviceIdString.value!!

        database.child("users").child(userId).child("devices")
                .child(deviceid).child("connection")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == null) {
                            setupCodeText.setText(getString(R.string.error_node_doesnt_exit))
                            return
                        }

                        val deviceHash = snapshot.value as String
                        if (deviceHash == "setup") {
                            firstTimeSetup()
                        } else {
                            setCodeEditTextRemoveHeader(deviceHash)
                        }
                    }

                })

    }

    private fun firstTimeSetup() {
        val hashids = Hashids(deviceid)
        val code: String = hashids.encode((10000..99999).random().toLong())

        database.child("users").child(userId)
                .child("devices").child(deviceid)
                .child("connection").setValue("1wifi:$code")
                .addOnSuccessListener {
                    setupCodeText.setText(code)
                }
                .addOnFailureListener {
                    setupCodeText.setText(getString(R.string.an_error_occured))
                }

    }

    private fun setCodeEditTextRemoveHeader(code: String) {
        val newcode = code.substring(code.indexOf(":")+1)
        setupCodeText?.setText(newcode)
    }

}