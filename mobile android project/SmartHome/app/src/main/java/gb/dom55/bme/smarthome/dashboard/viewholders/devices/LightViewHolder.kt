package gb.dom55.bme.smarthome.dashboard.viewholders.devices

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.model.devices.DeviceType
import gb.dom55.bme.smarthome.model.devices.Light
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener

class LightViewHolder(itemView: View, var clickListener: DeviceItemClickListener? = null)
    : BaseViewHolder(itemView) {


    var container: View
    var lightSwitch: ImageButton
    var image: LottieAnimationView
    var name: TextView

    private lateinit var deviceData: Light
    private lateinit var dataRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    init {
        lightSwitch = itemView.findViewById(R.id.dashItemLightSwitch)
        image = itemView.findViewById(R.id.dashItemLightImage)
        name = itemView.findViewById(R.id.dashItemLightName)
        container = itemView
    }

    override fun setState(deviceId: String) {
        auth = FirebaseAuth.getInstance()
        dataRef = FirebaseDatabase.getInstance().reference
                .child("devices/${deviceId}")

        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(Light::class.java)?.let {
                    deviceData = it
                }
                updateView()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dataRef.addListenerForSingleValueEvent(dataListener)
        dataRef.addValueEventListener(dataListener)


        container.setOnClickListener { clickListener?.onDeviceItemClick(deviceData) }


        lightSwitch.setOnClickListener {
            deviceData.on = !deviceData.on
            updateView()
            dataRef.child("on").setValue(deviceData.on)
        }

    }

    private fun updateView() {
        setImage()
        name.setText(deviceData.name)
    }

    private fun setImage() {
        when (deviceData.deviceType) {
            DeviceType.SOCKET -> {
                image.setImageResource(if (deviceData.on) {
                    R.drawable.ic_socket_on
                } else {
                    R.drawable.ic_socket_off
                })
            }
            DeviceType.KETTLE -> {
                image.setImageResource(R.drawable.ic_kettle)
            }
            DeviceType.AIR_CON -> {
                image.setImageResource(if (deviceData.on) {
                    R.drawable.ic_aircon_on
                } else {
                    R.drawable.ic_aircon_off
                })
            }
            DeviceType.DOOR_LOCK -> {
                image.setImageResource(if (deviceData.on) {
                    R.drawable.ic_door_locked
                } else {
                    R.drawable.ic_door_unlocked
                })
            }
            else -> {
                if (!deviceData.on) {
                    image.speed = -1.0f
                    image.playAnimation()
                } else {
                    image.speed = 1.0f
                    image.playAnimation()
                }
            }
        }
    }

}