package gb.dom55.bme.smarthome.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import gb.dom55.bme.smarthome.model.devices.*
import gb.dom55.bme.smarthome.model.devices.DeviceType
import gb.dom55.bme.smarthome.model.scenes.SceneDatabase
import gb.dom55.bme.smarthome.model.scenes.SceneItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val sceneDao = SceneDatabase.getDatabase(application).sceneIdDao()

    val sceneIds: MutableLiveData<MutableList<SceneItem>> by lazy {
        MutableLiveData<MutableList<SceneItem>>()
    }

    val sensors: MutableLiveData<MutableList<AbstractDevice>> by lazy {
        MutableLiveData<MutableList<AbstractDevice>>()
    }

    val devices: MutableLiveData<MutableList<AbstractDevice>> by lazy {
        MutableLiveData<MutableList<AbstractDevice>>()
    }

    suspend fun loadDatabases() {
        GlobalScope.launch(Dispatchers.Main) {
            val scenesLoaded = async(Dispatchers.IO) { sceneDao.getAllScenes(FirebaseAuth.getInstance().uid!!) }
            sceneIds.value = scenesLoaded.await()
        }
    }

    fun persistNewSceneOrder(scenes: MutableList<SceneItem>) {
        GlobalScope.launch(Dispatchers.IO) { sceneDao.reOrder(scenes) }
    }

    fun insertScene(sceneItem: SceneItem) {
        sceneItem.positionInRV = sceneDao.getLargestPosition(FirebaseAuth.getInstance().currentUser?.uid!!)
        sceneIds.value?.add(sceneItem)
        sceneDao.insert(sceneItem)
    }

    fun updateSceneName(sceneItem: SceneItem) {
        sceneIds.value?.find {
            it == sceneItem
        }?.name = sceneItem.name
        sceneDao.update(sceneItem)
    }

    fun updateScenes(scenes: MutableList<SceneItem>) {
        GlobalScope.launch(Dispatchers.IO) {
            for (scene in scenes) {
                sceneDao.update(scene)
            }
        }
    }

    fun deleteScene(sceneItem: SceneItem) {
        sceneDao.delete(sceneItem)
        sceneIds.value?.remove(sceneItem)
    }

    fun enableNotifications() {
        sensors.value?.let {
            for (id in it) {
                FirebaseMessaging.getInstance().subscribeToTopic(id.deviceid)
            }
        }
    }

    fun disableNotifications() {
        sensors.value?.let {
            for (id in it) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(id.deviceid)
            }
        }
    }

    private val userDevicesRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .child("devices")

    private var childEventListener: ChildEventListener? = null

    fun initFirebaseListener() {
        devices.value = mutableListOf()
        sensors.value = mutableListOf()
        childEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                addDeviceFromSnapshot(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                // Adding a new device will call this function twice
                // and all the data needed will be in the second call
                addDeviceFromSnapshot(snapshot)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                removeDeviceFromSnapshot(snapshot)
            }

            override fun onChildMoved(p0: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(p0: DatabaseError) {
                p0.toException().printStackTrace()
            }
        }

    }

    fun startFirebaseListener() {
        childEventListener?.let{
            userDevicesRef.addChildEventListener(it)
        }
    }

    fun stopFirebaseListener() {
        childEventListener?.let{
            userDevicesRef.removeEventListener(it)
        }
    }

    private fun addDeviceFromSnapshot(snapshot: DataSnapshot) {

        val device = getDeviceFactoryFromSnapshot(snapshot) ?: return

        FirebaseDatabase.getInstance().reference
            .child("devices")
            .child(device.deviceid)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(deviceSnapshot: DataSnapshot) {
                    device.getDataFromSnapshot(deviceSnapshot).let {
                        if (it.hasDashboardView()) {
                            devices.value?.add(it)
                            devices.value = devices.value
                        }
                        if (it.hasStatusView()) {
                            sensors.value?.add(it)
                            sensors.value = sensors.value
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    databaseError.toException().printStackTrace()
                }

            })
    }

    private fun removeDeviceFromSnapshot(snapshot: DataSnapshot) {

        val device = getDeviceFactoryFromSnapshot(snapshot) ?: return

        if (device.hasDashboardView()) {
            devices.value?.removeAll { it.deviceid == device.deviceid }
        }
        if (device.hasStatusView()) {
            sensors.value?.removeAll { it.deviceid == device.deviceid }
        }
    }

    private fun getDeviceFactoryFromSnapshot(snapshot: DataSnapshot): AbstractDevice? {
        if (!snapshot.child("id").exists() || !snapshot.child("type").exists()) {
            return null
        }

        val uid = FirebaseAuth.getInstance().uid!!
        val deviceId = snapshot.child("id").getValue(String::class.java)!!
        val type = snapshot.child("type").getValue(Int::class.java)!!
        val deviceType = DeviceType.values().find { it.type == type }

        deviceType?.let {
            return when (it) {
                DeviceType.RGBLIGHT -> RgbLight(uid, deviceId)
                DeviceType.LIGHT -> Light(uid, deviceId)
                DeviceType.DIMMABLE_LIGHT -> DimmableLight(uid, deviceId)
                DeviceType.BOOL_SENSOR -> BooleanSensor(uid, deviceId)
                DeviceType.INT_SENSOR -> IntegerSensor(uid, deviceId)
                DeviceType.NULL_DEVICE -> null
                DeviceType.KETTLE -> Light(uid, deviceId)
                DeviceType.SOCKET -> Light(uid, deviceId)
                DeviceType.AIR_CON -> Light(uid, deviceId)
                DeviceType.DOOR_LOCK -> Light(uid, deviceId)
                DeviceType.IR_REMOTE -> IrRemote(uid, deviceId)
                DeviceType.RGB_IR_REMOTE -> IrRemote(uid, deviceId)
                DeviceType.TV_REMOTE -> IrRemote(uid, deviceId)
                DeviceType.COFFEE -> CoffeeMaker(uid, deviceId)
                DeviceType.BLINDS -> DimmableLight(uid, deviceId)
                DeviceType.RADIATOR -> DimmableLight(uid, deviceId)
                DeviceType.DOOR_SENSOR -> BooleanSensor(uid, deviceId)
                DeviceType.MOTION_SENSOR -> BooleanSensor(uid, deviceId)
                DeviceType.WATER_PRESENCE_SENSOR -> BooleanSensor(uid, deviceId)
                DeviceType.CELSIUS_SENSOR -> IntegerSensor(uid, deviceId)
                DeviceType.HUMIDITY_SENSOR -> IntegerSensor(uid, deviceId)
                DeviceType.WATER_LEVEL_SENSOR -> IntegerSensor(uid, deviceId)
                DeviceType.MOISTURE_SENSOR -> IntegerSensor(uid, deviceId)
                DeviceType.THERMOSTAT -> Thermostat(uid, deviceId)
            }
        }
        return null
    }

    fun deleteDevice(id: String) {

        val database = FirebaseDatabase.getInstance().reference
        database.child("devices")
                .child(id)
                .removeValue()

        database.child("users")
                .child(FirebaseAuth.getInstance().uid!!)
                .child("devices")
                .child(id)
                .removeValue()

        FirebaseMessaging.getInstance().unsubscribeFromTopic(id)
    }

}

