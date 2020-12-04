package gb.dom55.bme.smarthome.devicecontrol.viewholders.notifications

import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import gb.dom55.bme.smarthome.model.notification.BooleanNotificationProperty
import gb.dom55.bme.smarthome.model.notification.NotificationPropertyBase
import gb.dom55.bme.smarthome.R

class
BooleanViewHolder(itemView: View, context: Context) : NotificationViewHolder(itemView, context) {
    private lateinit var boolNotifProp: BooleanNotificationProperty
    private var firebaseIndex = -1
    private var deviceId = ""

    private var notificationNameEditText: EditText = itemView.findViewById(R.id.boolNotifNameEdit)
    private var isActiveSwitch: Switch = itemView.findViewById(R.id.boolNotifActiveSwitch)
    private var trueCheckBox: CheckBox = itemView.findViewById(R.id.boolNotifTrueCheckBox)
    private var falseCheckBox: CheckBox = itemView.findViewById(R.id.boolNotifFalseCheckbox)
    private var trueMessageButton: Button = itemView.findViewById(R.id.boolNotifTrueMessageEditButton)
    private var falseMessageButton: Button = itemView.findViewById(R.id.boolNotifFalseMessageEditButton)
    private var trueMessageText: TextView = itemView.findViewById(R.id.boolNotifTrueMessageText)
    private var falseMessageText: TextView = itemView.findViewById(R.id.boolNotifFalseMessageText)
    private var labelText: TextView = itemView.findViewById(R.id.boolNotifLabel)

    override fun bindViewHolder(notifProperty: NotificationPropertyBase, index: Int, deviceId: String) {
        boolNotifProp = notifProperty as BooleanNotificationProperty
        firebaseIndex = index
        this.deviceId = deviceId
        updateView(boolNotifProp)
        setVisibility()
        setListeners(notifProperty)

    }

    private fun updateView(notifProperty: BooleanNotificationProperty) {
        trueCheckBox.isChecked = notifProperty.notifyWhenTrue
        falseCheckBox.isChecked = notifProperty.notifyWhenFalse
        isActiveSwitch.isChecked = notifProperty.isActive
        trueMessageText.setText(notifProperty.trueMessage)
        falseMessageText.setText(notifProperty.falseMessage)
        notificationNameEditText.setText(notifProperty.propertyName)
    }

    private fun setListeners(notifProperty: NotificationPropertyBase) {
        isActiveSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != boolNotifProp.isActive) {
                boolNotifProp.isActive = isChecked
                setVisibility()
                updateFirebase(notifProperty)
            }
        }

        trueCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != boolNotifProp.notifyWhenTrue) {
                boolNotifProp.notifyWhenTrue = isChecked
                setVisibility()
                updateFirebase(notifProperty)
            }
        }

        falseCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != boolNotifProp.notifyWhenFalse) {
                boolNotifProp.notifyWhenFalse = isChecked
                setVisibility()
                updateFirebase(notifProperty)
            }
        }

        notificationNameEditText.doAfterTextChanged {
            boolNotifProp.propertyName = it.toString()
            updateFirebase(notifProperty)
        }

        trueMessageButton.setOnClickListener {
            showMessageEditDialog(true)
        }

        falseMessageButton.setOnClickListener {
            showMessageEditDialog(false)
        }
    }

    private fun setVisibility() {
        if (boolNotifProp.isActive) {
            isActiveSwitch.isActivated = true
            labelText.visibility = View.VISIBLE
            trueCheckBox.visibility = View.VISIBLE
            setTrueMessageVisibility(boolNotifProp.notifyWhenTrue)
            falseCheckBox.visibility = View.VISIBLE
            setFalseMessageVisibility(boolNotifProp.notifyWhenFalse)

        } else {
            isActiveSwitch.isActivated = false
            labelText.visibility = View.GONE
            trueCheckBox.visibility = View.GONE
            falseCheckBox.visibility = View.GONE
            trueMessageButton.visibility = View.GONE
            trueMessageText.visibility = View.GONE
            falseMessageButton.visibility = View.GONE
            falseMessageText.visibility = View.GONE
        }
    }

    private fun setTrueMessageVisibility(isVisible: Boolean) {
        if (isVisible) {
            trueMessageButton.visibility = View.VISIBLE
            trueMessageText.visibility = View.VISIBLE
            trueMessageText.text = context.getString(R.string.notif_message, boolNotifProp.trueMessage)
        } else {
            trueMessageButton.visibility = View.GONE
            trueMessageText.visibility = View.GONE
        }
    }

    private fun setFalseMessageVisibility(isVisible: Boolean) {
        if (isVisible) {
            falseMessageButton.visibility = View.VISIBLE
            falseMessageText.visibility = View.VISIBLE
            falseMessageText.text = context.getString(R.string.notif_message, boolNotifProp.falseMessage)
        } else {
            falseMessageButton.visibility = View.GONE
            falseMessageText.visibility = View.GONE
        }
    }

    private fun updateFirebase(notifProperty: NotificationPropertyBase) {
        notifProperty.updateFirebaseRecord(deviceId, firebaseIndex)
    }

    private fun showMessageEditDialog(isTrueMessage: Boolean) {
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.setTitle(context.getString(R.string.edit_message))

        alertDialog.setMessage(context.getString(R.string.notification_message_explanation))

        val input = EditText(context)
        input.setText(if (!isTrueMessage) { boolNotifProp.falseMessage } else { boolNotifProp.trueMessage })

        val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp

        alertDialog.setView(input)
        alertDialog.setPositiveButton(
                context.getString(R.string.save)
        ) { _, _ ->
            if (input.text.toString() != "") {
                if (!isTrueMessage) {
                    boolNotifProp.falseMessage = input.text.toString()
                } else {
                    boolNotifProp.trueMessage = input.text.toString()
                }
                updateFirebase(boolNotifProp)
                setVisibility()
            }
        }
        alertDialog.setNegativeButton(
                context.getString(R.string.cancel)
        ) { dialog, _ -> dialog?.dismiss() }
        alertDialog.show()
    }
}