package gb.dom55.bme.smarthome.devicecontrol

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.devicecontrol.fragments.NotificationFragment
import gb.dom55.bme.smarthome.devicecontrol.fragments.SetupFragment
import gb.dom55.bme.smarthome.getDarkModeSharedPref
import kotlinx.android.synthetic.main.activity_device_controller.*

class DeviceControllerActivity : AppCompatActivity() {

    val model: DeviceControlViewModel by viewModels()
    private lateinit var deviceFragment: Fragment
    private lateinit var device: AbstractDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(if (getDarkModeSharedPref(this)){
            R.style.DarkTheme
        } else {
            R.style.LightTheme
        })
        setContentView(R.layout.activity_device_controller)

        val receivedData = intent.getSerializableExtra("DeviceData")
                ?: throw IllegalStateException("No device passed to activity")
        device = receivedData as AbstractDevice
        deviceFragment = device.getAssociatedFragment()

        pager.adapter = PagerAdapter(supportFragmentManager)
        model.initializeData(receivedData)
        model.initializeListener()

    }

    override fun onPause() {
        model.stopListener()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        model.startListener()
    }

    inner class PagerAdapter(manager: FragmentManager)
        : FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private val NUM_PAGES = 3

        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> deviceFragment
                1 -> SetupFragment()
                2 -> NotificationFragment()
                else -> throw IllegalStateException("Only 3 pages are allowed here")
            }
        }

        override fun getCount() : Int {
            return if (device.getNotificationProperties().size == 0){
                NUM_PAGES-1
            } else {
                NUM_PAGES
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}