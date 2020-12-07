package gb.dom55.bme.smarthome.dashboard.viewholders.devices

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.model.devices.IrRemote
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener

class IrRemoteViewHolder(itemView: View, var clickListener: DeviceItemClickListener? = null)
    : BaseViewHolder(itemView) {

    var container: View = itemView
    var name: TextView = itemView.findViewById(R.id.dashItemLightName)
    var upBtn: Button = itemView.findViewById(R.id.itemIRupButton)
    var downBtn: Button = itemView.findViewById(R.id.itemIRdownButton)
    var muteBtn: Button = itemView.findViewById(R.id.itemIRmuteButton)
    var powerBtn: ImageButton = itemView.findViewById(R.id.dashItemLightSwitch)

    private lateinit var deviceData: IrRemote
    private lateinit var dataRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun setState(deviceId: String) {
        auth = FirebaseAuth.getInstance()
        dataRef = FirebaseDatabase.getInstance().reference
                .child("devices/${deviceId}")

        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(IrRemote::class.java)?.let {
                    deviceData = it
                    updateView()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                deviceData = IrRemote("", deviceId)
            }
        }
        dataRef.addListenerForSingleValueEvent(dataListener)
        dataRef.addValueEventListener(dataListener)

        container.setOnClickListener { clickListener?.onDeviceItemClick(deviceData) }

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateView() {

        val touchListener = object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_UP ->{
                        deviceData.currentButtonCode = ""
                        FirebaseDatabase.getInstance().reference
                                .child("devices").child(deviceData.deviceid)
                                .child("currentButton").setValue("")
                    }
                    MotionEvent.ACTION_DOWN -> {
                        val code = when(v?.id) {
                            upBtn.id -> deviceData.codeMapping["D3"]
                            downBtn.id -> deviceData.codeMapping["D4"]
                            muteBtn.id -> deviceData.codeMapping["D5"]
                            powerBtn.id -> deviceData.codeMapping["A1"]
                            else -> null
                        }
                        code?.let {
                            deviceData.currentButtonCode = it
                        }
                        updateCode()
                    }
                }
                return false
            }
        }


        upBtn.setOnTouchListener(touchListener)
        downBtn.setOnTouchListener(touchListener)
        muteBtn.setOnTouchListener(touchListener)
        powerBtn.setOnTouchListener(touchListener)

        name.setText(deviceData.name)
        upBtn.setText(deviceData.buttonMapping["D3"])
        downBtn.setText(deviceData.buttonMapping["D4"])
        muteBtn.setText(deviceData.buttonMapping["D5"])

    }

    fun updateCode() {
        FirebaseDatabase.getInstance().reference
                .child("devices").child(deviceData.deviceid)
                .child("currentButton").setValue(deviceData.currentButtonCode)
    }

}