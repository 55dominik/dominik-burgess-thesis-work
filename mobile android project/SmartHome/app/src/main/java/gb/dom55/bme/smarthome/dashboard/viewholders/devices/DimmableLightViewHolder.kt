package gb.dom55.bme.smarthome.dashboard.viewholders.devices

import android.view.View
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.lukelorusso.verticalseekbar.VerticalSeekBar
import gb.dom55.bme.smarthome.model.devices.DimmableLight
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.model.devices.DeviceType

class DimmableLightViewHolder(itemView: View, var clickListener: DeviceItemClickListener? = null)
    : BaseViewHolder(itemView) {

    private var lightSlider: VerticalSeekBar = itemView.findViewById(R.id.dashItemDimLightSeekbar)
    private var image: LottieAnimationView = itemView.findViewById(R.id.dashItemDimLightImage)
    var name: TextView = itemView.findViewById(R.id.dashItemDimLightName)
    var value: TextView = itemView.findViewById(R.id.dashItemDimLightValue)

    var container = itemView
    var initialize: Boolean = true
    private lateinit var deviceData: DimmableLight
    private lateinit var dataRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun setState(deviceId: String) {
        auth = FirebaseAuth.getInstance()
        dataRef = FirebaseDatabase.getInstance().reference
                .child("devices/${deviceId}")

        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(DimmableLight::class.java)?.let {
                    deviceData = it
                }
                if (initialize) { setSeekBarInitialPosition() ; initialize = false}
                updateView()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        dataRef.addListenerForSingleValueEvent(dataListener)
        dataRef.addValueEventListener(dataListener)

        lightSlider.setOnProgressChangeListener { progress ->

            when (deviceData.deviceType) {
                DeviceType.BLINDS -> {
                    deviceData.brightness = progress.toDouble()
                    deviceData.on = progress > 10
                }

                else -> {
                    deviceData.brightness = progress.toDouble()
                    deviceData.on = progress > 30
                }
            }
            updateView()
            dataRef.child("on").setValue(deviceData.on)
            dataRef.child("brightness").setValue(deviceData.brightness)
        }

        container.setOnClickListener { clickListener?.onDeviceItemClick(deviceData) }

    }

    private fun setSeekBarInitialPosition() {
        lightSlider.progress = deviceData.brightness.toInt()
    }

    private fun updateView() {
        setImage()
        value.setText(deviceData.brightness.toInt().toString())
        name.setText(deviceData.name)
    }

    private fun setImage() {
        when (deviceData.deviceType) {
            DeviceType.BLINDS -> {
                image.setImageResource(
                        if (deviceData.on) {
                            when (deviceData.brightness.toInt()) {
                                in 0..33 -> {
                                    R.drawable.ic_blinds_low
                                }
                                in 34..66 -> {
                                    R.drawable.ic_blinds_middle
                                }
                                else -> {
                                    R.drawable.ic_blinds_high
                                }
                            }
                        } else {
                            R.drawable.ic_blinds_low
                        }
                )
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