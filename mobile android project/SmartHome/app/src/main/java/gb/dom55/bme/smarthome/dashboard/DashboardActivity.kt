package gb.dom55.bme.smarthome.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import gb.dom55.bme.smarthome.*
import gb.dom55.bme.smarthome.devicecontrol.DeviceControllerActivity
import gb.dom55.bme.smarthome.model.devices.AbstractDevice
import gb.dom55.bme.smarthome.dashboard.fragments.DevicesFragment
import gb.dom55.bme.smarthome.dashboard.fragments.ScenesFragment
import gb.dom55.bme.smarthome.dashboard.fragments.SettingsFragment
import gb.dom55.bme.smarthome.login.LogonActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    val model: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(
                if (getDarkModeSharedPref(this))
                    R.style.DarkTheme
                else
                    R.style.LightTheme
        )
        setContentView(R.layout.activity_dashboard)

        if (savedInstanceState != null) {
            if (!savedInstanceState.getBoolean("DARK_MODE_CHANGE")) {
                GlobalScope.launch(Dispatchers.Main) {
                    loadData()
                }
            }
        } else {
            GlobalScope.launch(Dispatchers.Main) {
                loadData()
            }
        }

        dashBottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.navigationDevices -> {
                    updateUI(DevicesFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigationScenes -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        GlobalScope.async(Dispatchers.IO) {
                            model.loadDatabases()
                        }.await()
                        updateUI(ScenesFragment())
                    }
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigationSettings -> {
                    updateUI(SettingsFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("DARK_MODE_CHANGE", true)
        super.onSaveInstanceState(outState)
    }

    private suspend fun loadData() {
        GlobalScope.async(Dispatchers.Main) {
            model.loadDatabases()
        }.await()
        if (getNotificationSharedPref(this)) {
            model.enableNotifications()
        } else {
            model.disableNotifications()
        }

        updateUI(DevicesFragment())
    }



    private fun updateUI(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
                .replace(R.id.dashContent, fragment)

                .commit()
        supportFragmentManager.executePendingTransactions()

    }

    fun navigateToDeviceControl(abstractDeviceData: AbstractDevice) {

        val controllerActivity = DeviceControllerActivity::class.java

        val intent = Intent(this@DashboardActivity, controllerActivity)
        intent.putExtra("DeviceData", abstractDeviceData)
        startActivity(intent)
    }

    fun navigateToLoginScreen() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this@DashboardActivity, LogonActivity::class.java)
        startActivity(intent)
        finish()
    }

}
