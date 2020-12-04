package gb.dom55.bme.smarthome.devicecontrol.fragments.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.devicecontrol.dialogs.SetIRCodesDialog
import gb.dom55.bme.smarthome.model.devices.AbstractDevice
import gb.dom55.bme.smarthome.model.devices.DeviceType
import gb.dom55.bme.smarthome.model.devices.IrRemote
import kotlinx.android.synthetic.main.fragment_ir_remote.*


class IrRemoteControlFragment : DeviceControlFragmentBase() , View.OnTouchListener, SetIRCodesDialog.SetCodesListener {

    private lateinit var remote : IrRemote

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {
        return inflater.inflate(R.layout.fragment_ir_remote, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.deviceData.observe(viewLifecycleOwner, Observer<AbstractDevice> {
            val newData = it as IrRemote
            remote = newData
            if (firstRun) {
                firstRun = false
                setupUI(newData.deviceType)
            }
            refreshUI(newData)
        })

    }

    private fun setupUI(type: DeviceType) {

        setButtons(type)
        irSetCodesButton.setOnClickListener {
            showSetCodesDialog()
        }


        irRemoteName.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                remote.name = irRemoteName.text.toString()
                model.updateValue(remote)
                irRemoteName.setSelection(irRemoteName.text.length)
            }
        }

    }

    private fun showSetCodesDialog() {
        val dialog = SetIRCodesDialog(remote, this)
        dialog.show(activity?.supportFragmentManager!!, "SetIRCodesDialogFragment")
    }

    private fun setButtons(type: DeviceType) {

        irButtonA1.text = (remote.buttonMapping["A1"])
        irButtonA2.text = (remote.buttonMapping["A2"])
        irButtonA3.text = (remote.buttonMapping["A3"])
        irButtonA4.text = (remote.buttonMapping["A4"])
        irButtonA5.text = (remote.buttonMapping["A5"])

        irButtonB1.text = (remote.buttonMapping["B1"])
        irButtonB2.text = (remote.buttonMapping["B2"])
        irButtonB3.text = (remote.buttonMapping["B3"])
        irButtonB4.text = (remote.buttonMapping["B4"])
        irButtonB5.text = (remote.buttonMapping["B5"])

        irButtonC1.text = (remote.buttonMapping["C1"])
        irButtonC2.text = (remote.buttonMapping["C2"])
        irButtonC3.text = (remote.buttonMapping["C3"])
        irButtonC4.text = (remote.buttonMapping["C4"])
        irButtonC5.text = (remote.buttonMapping["C5"])

        irButtonD1.text = (remote.buttonMapping["D1"])//up
        irButtonD2.text = (remote.buttonMapping["D2"])//down
        irButtonD3.text = (remote.buttonMapping["D3"])//vol up
        irButtonD4.text = (remote.buttonMapping["D4"])//vol down
        irButtonD5.text = (remote.buttonMapping["D5"])//mute

        irButtonA1.setOnTouchListener(this)
        irButtonA2.setOnTouchListener(this)
        irButtonA3.setOnTouchListener(this)
        irButtonA4.setOnTouchListener(this)
        irButtonA5.setOnTouchListener(this)
        irButtonB1.setOnTouchListener(this)
        irButtonB2.setOnTouchListener(this)
        irButtonB3.setOnTouchListener(this)
        irButtonB4.setOnTouchListener(this)
        irButtonB5.setOnTouchListener(this)
        irButtonC1.setOnTouchListener(this)
        irButtonC2.setOnTouchListener(this)
        irButtonC3.setOnTouchListener(this)
        irButtonC4.setOnTouchListener(this)
        irButtonC5.setOnTouchListener(this)
        irButtonD1.setOnTouchListener(this)
        irButtonD2.setOnTouchListener(this)
        irButtonD3.setOnTouchListener(this)
        irButtonD4.setOnTouchListener(this)
        irButtonD5.setOnTouchListener(this)

    }

    private fun refreshUI(new: IrRemote) {
        if (new.name != irRemoteName?.text.toString()) { irRemoteName?.setText(new.name) }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP ->{
                remote.currentButtonCode = ""
                model.updateValue(remote)
            }
            MotionEvent.ACTION_DOWN -> {
                val code = when(v?.id) {
                    irButtonA1.id -> remote.codeMapping["A1"]
                    irButtonA2.id -> remote.codeMapping["A2"]
                    irButtonA3.id -> remote.codeMapping["A3"]
                    irButtonA4.id -> remote.codeMapping["A4"]
                    irButtonA5.id -> remote.codeMapping["A5"]

                    irButtonB1.id -> remote.codeMapping["B1"]
                    irButtonB2.id -> remote.codeMapping["B2"]
                    irButtonB3.id -> remote.codeMapping["B3"]
                    irButtonB4.id -> remote.codeMapping["B4"]
                    irButtonB5.id -> remote.codeMapping["B5"]

                    irButtonC1.id -> remote.codeMapping["C1"]
                    irButtonC2.id -> remote.codeMapping["C2"]
                    irButtonC3.id -> remote.codeMapping["C3"]
                    irButtonC4.id -> remote.codeMapping["C4"]
                    irButtonC5.id -> remote.codeMapping["C5"]

                    irButtonD1.id -> remote.codeMapping["D1"]
                    irButtonD2.id -> remote.codeMapping["D2"]
                    irButtonD3.id -> remote.codeMapping["D3"]
                    irButtonD4.id -> remote.codeMapping["D4"]
                    irButtonD5.id -> remote.codeMapping["D5"]
                    else -> null
                }
                code?.let {
                    remote.currentButtonCode = it
                }
                model.updateValue(remote)
            }
        }


        return false
    }

    override fun updateCodes(codesMap: MutableMap<String, String>) {
        for (item in codesMap) {
            remote.codeMapping[item.key] = item.value
        }
        model.updateValue(remote)
    }


}