package gb.dom55.bme.smarthome.dashboard.viewholders.sensors

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.dashboard.viewholders.devices.BaseViewHolder
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.model.devices.CoffeeMaker
import gb.dom55.bme.smarthome.R

class CoffeeSensorViewHolder(itemView: View, var clickListener: DeviceItemClickListener? = null)
    : BaseViewHolder(itemView) {

    var name: TextView = itemView.findViewById(R.id.statusCoffeeName)
    var container: View = itemView
    private var waterImage: ImageView = itemView.findViewById(R.id.statusCoffeeWaterImage)
    private var cupImage: ImageView = itemView.findViewById(R.id.statusCoffeeCupImage)

    private lateinit var deviceData: CoffeeMaker
    private lateinit var dataRef : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun setState(deviceId: String) {
        auth = FirebaseAuth.getInstance()
        dataRef = FirebaseDatabase.getInstance().reference
                .child("devices/${deviceId}")

        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(CoffeeMaker::class.java)?.let {
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
            clickListener?.onDeviceItemClick(deviceData)
        }
    }

    private fun updateView() {
        setImages(deviceData)
        name?.setText(deviceData.name)
    }

    private fun setImages(deviceData: CoffeeMaker) {
        waterImage?.setImageResource(
            if (deviceData.enoughWater) {
                R.drawable.ic_water_present
            } else {
                R.drawable.ic_water_low
            }
        )
        cupImage?.setImageResource(
                if (deviceData.cupPresent) {
                    R.drawable.ic_cup_ok
                } else {
                    R.drawable.ic_cup_warn
                }
        )

    }
}