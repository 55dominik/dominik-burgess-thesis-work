package gb.dom55.bme.smarthome.nearby

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.DashboardActivity
import gb.dom55.bme.smarthome.getDarkModeSharedPref
import gb.dom55.bme.smarthome.nearby.listeners.NearbyEventListener
import kotlinx.android.synthetic.main.activity_nearby_connection_login.*

class NearbyConnectionLoginActivity : AppCompatActivity(), NearbyEventListener {

    companion object {
        const val PERMISSIONS_REQUEST_LOCATION = 100
    }

    private lateinit var user: FirebaseUser
    private lateinit var auth: FirebaseAuth
    private lateinit var connectedFlag: DatabaseReference
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var successListener: ValueEventListener

    private lateinit var nearbyDiscoverer : NearbyDiscovererManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(if (getDarkModeSharedPref(this)){
            R.style.DarkTheme
        } else {
            R.style.LightTheme
        })
        setContentView(R.layout.activity_nearby_connection_login)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!
        connectedFlag = FirebaseDatabase.getInstance().reference
                .child("users")
                .child(user.uid)
                .child("piConnected")

        successListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                        this@NearbyConnectionLoginActivity,
                        getString(R.string.an_error_occured),
                        Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val flagChangedByPi = snapshot.value as Boolean
                if (flagChangedByPi) {
                    finishSetup()
                }
            }

        }
        connectedFlag.addValueEventListener(successListener)

        intent.getStringExtra("email")?.let {
            email = it
        }
        intent.getStringExtra("password")?.let {
            password = it
        }


        handleLocationPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    nearbyDiscoverer = NearbyDiscovererManager(this, this)
                } else {
                    handleLocationPermission()
                }
            }
        }
    }

    private fun handleLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            showRationaleDialog(
                    title = getString(R.string.permission_needed),
                    explanation = getString(R.string.location_permission_explanation),
                    onPositiveButton = this::requestLocationPermission
            )
        } else {
            nearbyDiscoverer = NearbyDiscovererManager(this, this)
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun showRationaleDialog(
            title: String = getString(R.string.attention),
            explanation: String,
            onPositiveButton: () -> Unit,
            onNegativeButton: () -> Unit = this::finish
    ) {
        val alertDialog = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(explanation)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.continue_string)) { dialog, _ ->
                    dialog.cancel()
                    onPositiveButton()
                }
                .setNegativeButton(getString(R.string.exit)) { _, _ -> onNegativeButton() }
                .create()
        alertDialog.show()
    }

    override fun onDiscovered() {
        nearbyInformationBox.setText(getString(R.string.device_found))
    }

    override fun startedDiscovering() {
        nearbyInformationBox.setText(getString(R.string.searching_for_device))
    }

    override fun onConnected() {
        nearbyInformationBox.setText(getString(R.string.successfully_connected_to_pi))
        startLoginProcess()
    }

    override fun onFailure() {
        nearbyInformationBox.setText(getString(R.string.an_error_occured))
    }

    private fun startLoginProcess() {
        nearbyDiscoverer.sendDataEncrypted("$email $password")
        nearbyInformationBox.append("\n"+getString(R.string.signing_in_as, email))
    }

    private fun finishSetup() {
        connectedFlag.removeEventListener(successListener)
        nearbyInformationBox.setText(getString(R.string.setup_successful))
        button.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
        button.visibility = View.VISIBLE
        button.isEnabled = true
    }
}
