package gb.dom55.bme.smarthome.dashboard.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import gb.dom55.bme.smarthome.*
import gb.dom55.bme.smarthome.login.LogonActivity
import gb.dom55.bme.smarthome.dashboard.DashboardActivity
import gb.dom55.bme.smarthome.dashboard.DashboardViewModel

import kotlinx.android.synthetic.main.dialog_fragment_account_deletion.view.*
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    val model: DashboardViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsReconnectPiButton.setOnClickListener {
            reconnectPi()
        }

        settingsStatusCurrentEmail.text = getString(R.string.signed_in_as, FirebaseAuth.getInstance().currentUser?.email)
        settingsStatusTotalDevices.text = getString(R.string.number_of_devices_number, model.devices.value?.size?.plus(model.sensors.value?.size!!))

        settingsNightModeSwitch.setTexts(getString(R.string.dark), getString(R.string.light))
        settingsNightModeSwitch.setColorOn(R.color.colorAccent) // Only works with accent color

        settingsLogoutButton.setOnClickListener {
            (activity as DashboardActivity).navigateToLoginScreen()
        }

        settingsResetPasswordButton.setOnClickListener {
            val email = FirebaseAuth.getInstance().currentUser?.email
            if (email != null) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                Toast.makeText(activity, getString(R.string.reset_link_sent_to, email), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, getString(R.string.an_error_occured), Toast.LENGTH_LONG).show()
            }
        }

        settingsDeleteAccountButton.setOnClickListener {
            showDeleteConfirmDialog()
        }

        settingsChangeEmailButton.visibility = View.GONE

        settingsNightModeSwitch.isOn = getDarkModeSharedPref(requireContext())
        settingsNightModeSwitch.setOnToggledListener { _, isOn ->
            setDarkModeSharedPref(requireContext(), isOn)
            requireActivity().recreate()
        }

        settingsNotificationsSwitch.isOn = getNotificationSharedPref(requireContext())
        settingsNotificationsSwitch.setOnToggledListener { _, isOn ->
            setNotificationSharedPref(requireContext(), isOn)
            if (isOn) {
                model.enableNotifications()
            } else {
                model.disableNotifications()
            }
        }

        settingsEmailNotificationsSwitch.setOnToggledListener { _, isOn ->
            updateEmailPreferences(isOn)
        }

        FirebaseDatabase.getInstance().reference
                .child("users").child(FirebaseAuth.getInstance().currentUser?.uid!!)
                .child("receiveEmailUpdates")
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        settingsEmailNotificationsSwitch.isOn = false
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            settingsEmailNotificationsSwitch?.isOn = snapshot.getValue(Boolean::class.java)!!
                        } else {
                            settingsEmailNotificationsSwitch?.isOn = false
                        }
                    }
                })
    }

    private fun reconnectPi() {
        val alertDialog = AlertDialog.Builder(activity as Context, R.style.FullScreenDialog)
                .setTitle(getString(R.string.reconnect_pi_question))
                .setMessage(getString(R.string.reconnect_pi_warning))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.continue_string)) { dialog, _ ->

                    FirebaseDatabase.getInstance().reference
                            .child("users").child(FirebaseAuth.getInstance().currentUser?.uid!!)
                            .child("piConnected").setValue(false)
                            .addOnSuccessListener {
                                dialog.dismiss()
                                FirebaseAuth.getInstance().signOut()
                                val intent = Intent(activity, LogonActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), getString(R.string.pi_disconnect_failed), Toast.LENGTH_LONG).show()
                            }
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        alertDialog.show()
    }

    private fun updateEmailPreferences(receive: Boolean) {
        FirebaseDatabase.getInstance().reference
                .child("users").child(FirebaseAuth.getInstance().currentUser?.uid!!)
                .child("receiveEmailUpdates").setValue(receive)
    }


    private fun showDeleteConfirmDialog() {

        val inflater = LayoutInflater.from(activity)
        val dialogView = inflater.inflate(R.layout.dialog_fragment_account_deletion, null)
        val confirmNumber = (1000..9999).random()
        dialogView.dialogDeleteConfirmationNumbers.setText(confirmNumber.toString())

        val alertDialog = AlertDialog.Builder(activity as Context, R.style.FullScreenDialog)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.continue_string)) { dialog, _ ->
                    if (dialogView.dialogDeleteConfirmationNumberField.text.toString() != dialogView.dialogDeleteConfirmationNumbers.text.toString()) {
                        deleteDialogWrongNumbers()
                    } else {
                        deleteCurrentlyLoggedInAccountData()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
        alertDialog.show()
    }

    private fun deleteCurrentlyLoggedInAccountData() {
        Toast.makeText(activity, getString(R.string.confirmed_account_deletion), Toast.LENGTH_LONG).show()
        val dbRef = FirebaseDatabase.getInstance().reference

        if (model.devices.value != null) {
            for (device in model.devices.value!!) {
                dbRef.child("devices/${device.deviceid}").removeValue()
            }
        }

        if (model.sceneIds.value != null) {
            for (scene in model.sceneIds.value!!){
                dbRef.child("scenes/${scene.sceneId}").removeValue()
            }
        }
        if (model.sensors.value != null) {
            for (sensor in model.sensors.value!!){
                dbRef.child("devices/${sensor.deviceid}").removeValue()
            }
        }

        val fbUser = FirebaseAuth.getInstance().currentUser ?: return

        val userRef = dbRef.child("users/${fbUser.uid}")
        userRef.removeValue().addOnSuccessListener {
            fbUser.delete()
                    .addOnSuccessListener {
                        (activity as DashboardActivity).navigateToLoginScreen()

                        Log.d("delee", "onsuccess")
                    }
                    .addOnFailureListener {
                        Log.d("delee", "onfail")
                    }
        }
    }

    private fun deleteDialogWrongNumbers() {
        Toast.makeText(activity, getString(R.string.incorrect_code_entered), Toast.LENGTH_LONG).show()
    }

}
