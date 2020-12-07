package gb.dom55.bme.smarthome.dashboard.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import gb.dom55.bme.smarthome.dashboard.DashboardActivity
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.adapters.DevicesAdapter
import gb.dom55.bme.smarthome.dashboard.adapters.StatusAdapter
import gb.dom55.bme.smarthome.dashboard.adapters.touchhelpers.DevicesTouchHelperCallback
import gb.dom55.bme.smarthome.dashboard.adapters.touchhelpers.SensorTouchHelperCallback
import gb.dom55.bme.smarthome.dashboard.dialogs.NewDeviceDialogFragment
import gb.dom55.bme.smarthome.dashboard.listeners.DeviceItemClickListener
import gb.dom55.bme.smarthome.model.AbstractDevice
import gb.dom55.bme.smarthome.dashboard.DashboardViewModel
import gb.dom55.bme.smarthome.dashboard.listeners.NewDeviceListener
import kotlinx.android.synthetic.main.fragment_devices.*
import org.hashids.Hashids
import java.util.*

class DevicesFragment : Fragment(), DeviceItemClickListener, NewDeviceListener {

    companion object {
        private var NUM_COLS = 2
    }

    private lateinit var dashRecycleAdapter: DevicesAdapter
    private lateinit var statusRecycleAdapter: StatusAdapter
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    val model: DashboardViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_devices, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        model.initFirebaseListener()

        devicesFab.setOnClickListener {
            val dialog = NewDeviceDialogFragment(this)
            dialog.show(activity?.supportFragmentManager!!, "NewDeviceDialogFragmemt")
        }
        setGreeting()
        initRecyclerViews()
    }

    override fun onPause() {
        super.onPause()
        model.stopFirebaseListener()
    }

    private fun setGreeting() {
        val currentTime = Calendar.getInstance()
        val greeting = when(currentTime.get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> getString(R.string.greeting_morning)
            in 12..17 -> getString(R.string.greeting_afternoon)
            in 18..21 -> getString(R.string.greeting_evening)
            in 22..23 -> getString(R.string.greeting_night)
            in 0..4 -> getString(R.string.greeting_early_morning)
            else -> getString(R.string.greeting_all_day)
        }
        dashGreeting.setText(greeting)
    }

    private fun initRecyclerViews() {

        model.startFirebaseListener()

        NUM_COLS = when (requireActivity().resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_XLARGE ->  4
            Configuration.SCREENLAYOUT_SIZE_LARGE ->  3
            Configuration.SCREENLAYOUT_SIZE_NORMAL ->  2
            Configuration.SCREENLAYOUT_SIZE_SMALL -> 1
            else -> 2
        }

        val staggeredGridLayoutManager = StaggeredGridLayoutManager(NUM_COLS, LinearLayoutManager.VERTICAL)
        dashRecycler.layoutManager = staggeredGridLayoutManager
        dashRecycleAdapter = DevicesAdapter(requireContext())
        dashRecycleAdapter.clickListener = this

        model.devices.observe(viewLifecycleOwner, Observer<MutableList<AbstractDevice>> {
            dashRecycleAdapter.replaceDevices(it)
        })
        dashRecycler.adapter = dashRecycleAdapter
        val callbackDevice = DevicesTouchHelperCallback(dashRecycleAdapter)
        val touchHelper = ItemTouchHelper(callbackDevice)
        touchHelper.attachToRecyclerView(dashRecycler)


        dashStatusRecycler.layoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        statusRecycleAdapter = StatusAdapter(requireContext())
        statusRecycleAdapter.clickListener = this

        model.sensors.observe(viewLifecycleOwner, Observer<MutableList<AbstractDevice>> {
            statusRecycleAdapter.replaceDevices(it)
        })
        dashStatusRecycler.adapter = statusRecycleAdapter
        val callbackSensor = SensorTouchHelperCallback(statusRecycleAdapter)
        val touchHelperSensor = ItemTouchHelper(callbackSensor)
        touchHelperSensor.attachToRecyclerView(dashStatusRecycler)

    }

    override fun onDeviceItemClick(deviceData: AbstractDevice) {
        (activity as DashboardActivity).navigateToDeviceControl(deviceData)
    }

    override fun onDeviceDeleted(deviceData: AbstractDevice) {
        model.deleteDevice(deviceData.deviceid)
        if (deviceData.hasDashboardView()) {
            dashRecycleAdapter.removeDevice(deviceData)
        }

        if (deviceData.hasStatusView()) {
            statusRecycleAdapter.removeDevice(deviceData)
        }
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, name: String, factory: AbstractDevice) {

        val deviceID = firebaseDatabase.reference.child("devices").push().key!!
        val userID = firebaseAuth.currentUser?.uid!!

        val device = factory.createDevice(userID, deviceID, name)

        firebaseDatabase.reference.child("devices/${deviceID}").setValue(device.getDataClassFB())
        val userDeviceRef = firebaseDatabase.reference.child("users/${userID}/devices/${deviceID}")
        userDeviceRef.child("type").setValue(device.getType().type)
        userDeviceRef.child("typeName").setValue(device.getType())
        userDeviceRef.child("connection").setValue(genHashForDevice(deviceID))
        userDeviceRef.child("id").setValue(deviceID)

        if (device.hasStatusView()) {
            FirebaseMessaging.getInstance().subscribeToTopic(deviceID)
        }

        dialog.dismiss()
    }

    private fun genHashForDevice(deviceid: String) : String {
        val hashids = Hashids(deviceid)
        return hashids.encode((10000..99999).random().toLong())
    }

}
