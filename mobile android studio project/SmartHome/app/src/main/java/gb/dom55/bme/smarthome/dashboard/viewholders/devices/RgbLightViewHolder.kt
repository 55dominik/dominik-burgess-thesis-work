package gb.dom55.bme.smarthome.dashboard.viewholders.devices

import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.model.devices.RgbLight
import kotlinx.android.synthetic.main.item_dash_rgblight.view.*

class RgbLightViewHolder(itemView: View,  var clickListener: DeviceItemClickListener? = null)
    : BaseViewHolder(itemView) {

    var container: View = itemView
    var name: TextView = itemView.dashItemRgbLightName
    private var lightSwitch: ImageButton = itemView.dashItemRgbLightSwitch
    private var image: LottieAnimationView = itemView.dashItemRgbLightImage
    private var colourIndicator: View = itemView.dashItemRgbLightColour

    private lateinit var deviceData: RgbLight
    private lateinit var dataRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun setState(deviceId: String) {

        auth = FirebaseAuth.getInstance()
        dataRef = FirebaseDatabase.getInstance().reference
                .child("devices/${deviceId}")

        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(RgbLight::class.java)?.let {
                    deviceData = it
                }
                updateView()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dataRef.addListenerForSingleValueEvent(dataListener)
        dataRef.addValueEventListener(dataListener)


        container.setOnClickListener {
            clickListener?.onDeviceItemClick(deviceData) //Launch activity for device control
        }

        lightSwitch.setOnClickListener {
            deviceData.on = !deviceData.on
            updateView()
            dataRef.child("on").setValue(deviceData.on)
        }


    }

    private fun updateView() {
        if (!deviceData.on) {
            image.speed = -1.0f
            image.playAnimation()
        } else {
            image.speed = 1.0f
            image.playAnimation()
        }
        name.setText(deviceData.name)
        if (deviceData.white) {
            colourIndicator.setBackgroundColor(Color.LTGRAY)
        } else {
            colourIndicator.setBackgroundColor(Color.rgb(deviceData.red, deviceData.green, deviceData.blue))
        }
    }

}