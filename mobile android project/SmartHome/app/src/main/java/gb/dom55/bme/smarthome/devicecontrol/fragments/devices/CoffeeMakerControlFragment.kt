package gb.dom55.bme.smarthome.devicecontrol.fragments.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.model.devices.AbstractDevice
import gb.dom55.bme.smarthome.model.devices.CoffeeMaker
import kotlinx.android.synthetic.main.fragment_coffee_maker.*

class CoffeeMakerControlFragment : DeviceControlFragmentBase() {

    private lateinit var maker : CoffeeMaker

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.fragment_coffee_maker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.deviceData.observe(viewLifecycleOwner, Observer<AbstractDevice> {
            val newMaker = it as CoffeeMaker
            maker = newMaker
            if (firstRun) {
                firstRun = false
                setupUI()
            }
            refreshUI(newMaker)
        })
    }

    private fun setupUI() {

        makeCoffeeButton?.isEnabled = maker.cupPresent && maker.enoughWater

        makeCoffeeButton.setOnClickListener {
            maker.makeCoffee = true
            model.updateValue(maker)
        }


        coffeeMakerName.doAfterTextChanged {

        }

        coffeeMakerName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                maker.name = coffeeMakerName.text.toString()
                model.updateValue(maker)
                coffeeMakerName.setSelection(coffeeMakerName.text.length)
            }
        }

        setImages(maker)
        makeCoffeeButton?.isEnabled = maker.cupPresent && maker.enoughWater
        coffeeMakerName?.setText(maker.name)
    }

    private fun refreshUI(new: CoffeeMaker) {
        setImages(new)
        if (new.name != coffeeMakerName?.text.toString()) { coffeeMakerName?.setText(new.name) }
        makeCoffeeButton?.isEnabled = new.cupPresent && new.enoughWater
    }

    private fun setImages(device: CoffeeMaker) {

        waterStatusImage?.setImageResource(
            if (device.enoughWater) {
                R.drawable.ic_water_present
            } else {
                R.drawable.ic_water_low
            }
        )

        cupStatusImage?.setImageResource(
            if (device.cupPresent) {
                R.drawable.ic_cup_ok
            } else {
                R.drawable.ic_cup_warn
            }
        )

        if (device.makeCoffee) {
            makingAnimation?.playAnimation()
            makingAnimation?.speed = 1.0f
            makingAnimation?.repeatCount = 20
        } else {
            makingAnimation?.speed = 0.0f
            makingAnimation?.frame = 0
            makingAnimation?.repeatCount = 0
        }
    }

}