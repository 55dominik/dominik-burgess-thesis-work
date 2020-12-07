package gb.dom55.bme.smarthome.dashboard.viewholders.devices

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.model.devices.CoffeeMaker

class CoffeeMakerViewHolder(itemView: View, var clickListener: DeviceItemClickListener? = null)
    : BaseViewHolder(itemView) {

    var container: View = itemView
    var name: TextView = itemView.findViewById(R.id.dashItemCoffeeName)
    private var image: LottieAnimationView = itemView.findViewById(R.id.dashItemCoffeeImage)
    private var makeButton: ImageButton = itemView.findViewById(R.id.dashItemCoffeeButton)

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


        container.setOnClickListener { clickListener?.onDeviceItemClick(deviceData) }


        makeButton.setOnClickListener {
            deviceData.makeCoffee = true
            dataRef.child("makeCoffee").setValue(true)
            updateView()
        }
    }

    private fun updateView() {
        setImage()
        name.setText(deviceData.name)

        makeButton.isEnabled = deviceData.enoughWater && deviceData.cupPresent
    }

    private fun setImage() {
        if (deviceData.makeCoffee) {
            image.speed = 1.0f
            image.playAnimation()
            image.repeatCount = 20
        }
        else {
            image.speed = 0.0f
            image.frame = 0
        }
    }
}