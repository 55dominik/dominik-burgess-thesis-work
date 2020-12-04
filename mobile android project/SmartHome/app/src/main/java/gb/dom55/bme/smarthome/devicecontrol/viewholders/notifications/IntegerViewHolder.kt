package gb.dom55.bme.smarthome.devicecontrol.viewholders.notifications

import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import gb.dom55.bme.smarthome.model.notification.IntegerNotificationProperty
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.R

class IntegerViewHolder(itemView: View, context: Context) : NotificationViewHolder(itemView, context) {
    private lateinit var intNotifProp: IntegerNotificationProperty
    private var firebaseIndex = -1
    private var deviceId: String = ""

    private var notificationNameEditText: EditText = itemView.findViewById(R.id.intNotifNameEdit)
    private var isActiveSwitch: Switch = itemView.findViewById(R.id.intNotifActiveSwitch)
    private var radioGroup: RadioGroup = itemView.findViewById(R.id.intNotifRadioGroup)
    private var valueEditText: EditText = itemView.findViewById(R.id.intNotifValueEdit)
    private var valueLabel: TextView = itemView.findViewById(R.id.intNotifValueLabel)
    private var itemLabel: TextView = itemView.findViewById(R.id.intNotifLabel)

    private var messageLabel: TextView = itemView.findViewById(R.id.intNotifMessageLabel)
    private var messageEditButton: TextView = itemView.findViewById(R.id.intNotifMessageButton)

    private var greaterThan: RadioButton = itemView.findViewById(R.id.intNotifGreaterRadio)
    private var lessThan: RadioButton = itemView.findViewById(R.id.intNotifLessRadio)
    var equals: RadioButton = itemView.findViewById(R.id.intNotifEqualsRadio)


    override fun bindViewHolder(notifProperty: NotificationPropertyBase, index: Int, deviceId: String) {
        intNotifProp = notifProperty as IntegerNotificationProperty
        firebaseIndex = index
        this.deviceId = deviceId
        updateView(intNotifProp)
        setVisibility()
        setListeners(notifProperty)

    }

    private fun setListeners(notifProperty: NotificationPropertyBase) {
        messageEditButton.setOnClickListener {
            showEditMessageDialog()
        }

        isActiveSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (intNotifProp.isActive != isChecked) {
                intNotifProp.isActive = isChecked
                setVisibility()
                updateFirebase(notifProperty)
            }
        }

        notificationNameEditText.doAfterTextChanged {
            intNotifProp.propertyName = it.toString()
            updateFirebase(notifProperty)
        }

        valueEditText.doAfterTextChanged {
            try {
                intNotifProp.triggerValue = it.toString().toInt()
                updateFirebase(notifProperty)
            } catch (e: NumberFormatException) {
                valueEditText.setError(context.getString(R.string.please_enter_number))
            }

        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            intNotifProp.triggerCondition = when (checkedId) {
                R.id.intNotifEqualsRadio -> "EQ"
                R.id.intNotifLessRadio -> "LT"
                R.id.intNotifGreaterRadio -> "GT"
                else -> {
                    "EQ"
                }
            }
            updateFirebase(intNotifProp)
        }
    }

    private fun updateView(notifProperty: IntegerNotificationProperty) {
        isActiveSwitch.isChecked = notifProperty.isActive
        notificationNameEditText.setText(notifProperty.propertyName)
        valueEditText.setText(notifProperty.triggerValue.toString())
        when (notifProperty.triggerCondition) {
            "EQ" -> {
                equals.isChecked = true
            }
            "GT" -> {
                greaterThan.isChecked = true
            }
            "LT" -> {
                lessThan.isChecked = true
            }
        }
    }

    private fun setVisibility() {
        if (intNotifProp.isActive) {
            isActiveSwitch.isActivated = true
            radioGroup.visibility = View.VISIBLE
            itemLabel.visibility = View.VISIBLE
            valueLabel.visibility = View.VISIBLE
            valueEditText.visibility = View.VISIBLE
            messageLabel.visibility = View.VISIBLE
            messageEditButton.visibility = View.VISIBLE
            messageLabel.text = context.getString(R.string.notif_message, intNotifProp.message)

        } else {
            isActiveSwitch.isActivated = false
            radioGroup.visibility = View.GONE
            itemLabel.visibility = View.GONE
            valueLabel.visibility = View.GONE
            valueEditText.visibility = View.GONE
            messageLabel.visibility = View.GONE
            messageEditButton.visibility = View.GONE
        }
    }

    private fun showEditMessageDialog() {
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.setTitle(context.getString(R.string.edit_message))

        alertDialog.setMessage(context.getString(R.string.notification_message_explanation))

        val input = EditText(context)
        input.setText(intNotifProp.message)
        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp

        alertDialog.setView(input)
        alertDialog.setPositiveButton(
                context.getString(R.string.save)
        ) { _, _ ->
            if (input.text.toString() != "") {
                intNotifProp.message = input.text.toString()
                updateFirebase(intNotifProp)
                setVisibility()
            }
        }
        alertDialog.setNegativeButton(
                context.getString(R.string.cancel)
        ) { dialog, _ -> dialog?.dismiss() }
        alertDialog.show()
    }

    private fun updateFirebase(notifProperty: NotificationPropertyBase) {
        notifProperty.updateFirebaseRecord(deviceId, firebaseIndex)
    }

}