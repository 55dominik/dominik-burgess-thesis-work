package gb.dom55.bme.smarthome.model.scenes

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.model.devices.AbstractDevice

object SceneManager {

    private var restoreNode: String? = null

    private const val KEY = "PREFERENCE_KEY"
    private const val RESTORE_NODE_KEY = "SCENE_RESTORE_NODE_KEY"
    private const val CURRENT_ACTIVE_SCENE_KEY = "ACTIVE_SCENE_KEY"
    private const val TAG = "SceneManager"

    private val scenesRef = FirebaseDatabase.getInstance().reference.child("scenes")
    private val devicesRef = FirebaseDatabase.getInstance().reference.child("devices")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid!!

    fun activateScene(sceneItem: SceneItem, context: Context) {
        activateScene(sceneItem.sceneId, context)
    }

    fun activateScene(sceneId: String, context: Context) {

        restoreNode = getSceneRestore(context)

        val sceneDevicesRef = scenesRef.child(sceneId).child("deviceIds")
        val devicesInScene = mutableListOf<String>()
        sceneDevicesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (idSnapshot in snapshot.children) {
                    idSnapshot.getValue(String::class.java)?.let {
                        devicesInScene.add(it)
                    }
                }
                copyDeviceStates(devicesInScene, sceneId, context)
            }
            override fun onCancelled(p0: DatabaseError) {}
        })

    }

    private fun copyDeviceStates(deviceIds: MutableList<String>, sceneId: String, context: Context) {
        // Empty restore node
        if (restoreNode == null){
            Log.e(TAG, "Restore node shared preference string was null")
            return
        }
        scenesRef.child(restoreNode!!).child("sceneData").child("userId").setValue(userId)

        val currentlySavedDeviceIds = mutableListOf<String>()
        val currentlySavedDeviceIdsRef = scenesRef.child(restoreNode!!).child("deviceIds")
        currentlySavedDeviceIdsRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (idSnapshot in snapshot.children) {
                    idSnapshot.getValue(String::class.java)?.let {
                        currentlySavedDeviceIds.add(it)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.details+p0.message)
            }
        })


        for (id in deviceIds) {
            // Save data of devices in list from under devices to under restore node/devices (copy)
            // Only save if there isn't an active scene right now

            if (checkForActiveScene(context) == null)
                copyFirebaseDeviceNode(
                        fromPath = devicesRef.child(id),
                        toPath = scenesRef.child(restoreNode!!).child("devices").child(id)
                )

            // Copy device data from sceneid/devices to under devices
            copyFirebaseDeviceNode(
                    fromPath = scenesRef.child(sceneId).child("devices").child(id),
                    toPath = devicesRef.child(id)
            )
        }
        // Save deviceIds string list to restore node/deviceIds
        scenesRef.child(restoreNode!!).child("deviceIds").setValue(deviceIds)
        saveActiveScene(context, sceneId)

    }


    private fun copyFirebaseDeviceNode(fromPath: DatabaseReference, toPath: DatabaseReference, removeValue: Boolean = false) {
        val valueEventListener = object:  ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()){
                    return
                }
                toPath.setValue(snapshot.getValue()).addOnSuccessListener {
                    Log.d(TAG, "Node copy successful")
                    if (removeValue) {
                        fromPath.removeValue().addOnSuccessListener {
                            Log.d(TAG, "Node deleted successfully")
                        }
                        Log.d(TAG, fromPath.parent!!.parent!!.key!!)
                    }
                }.addOnFailureListener {
                    Log.d(TAG, "Node copy failed")
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        }
        fromPath.addListenerForSingleValueEvent(valueEventListener)
    }

    fun deactivateScene(sceneItem: SceneItem, context: Context) {
        deactivateScene(sceneItem.sceneId, context)
    }

    fun deactivateScene(sceneID: String? = null, context: Context) {
        saveActiveScene(context, "empty")
        restoreNode = getSceneRestore(context)
        if (restoreNode == null) {
            Log.e(TAG, "Restore node shared preference string was null")
            return
        }
        val restorableDeviceIds = mutableListOf<String>()

        // Get devices under restore node/devices into a string list and pass to restore state function
        val restoreRef = scenesRef.child(restoreNode!!).child("deviceIds")
        restoreRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (idSnapshot in snapshot.children) {
                    idSnapshot.getValue(String::class.java)?.let {
                        restorableDeviceIds.add(it)
                    }
                }

                restoreDeviceStates(sceneID, restorableDeviceIds)
                // If list is empty then no scene is active
            }
            override fun onCancelled(p0: DatabaseError) {}
        })


    }

    private fun restoreDeviceStates(sceneId: String? = null, deviceIds: MutableList<String> ) {

        // Use restore node to copy from under restore node/devices to under devices
        for (id in deviceIds) {
            sceneId?.let {
                copyFirebaseDeviceNode(
                        fromPath = devicesRef.child(id),
                        toPath = scenesRef.child(it).child("devices").child(id),
                        removeValue = false
                )
            }

            copyFirebaseDeviceNode(
                    fromPath = scenesRef.child(restoreNode!!).child("devices").child(id),
                    toPath = devicesRef.child(id),
                    removeValue = true
            )
        }

        scenesRef.child(restoreNode!!).child("deviceIds").removeValue()

    }


    private fun checkForSceneRestore(context: Context) : String? {
        val sharedPref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
        val defaultvalue = "empty"
        val restoreSceneId = sharedPref.getString(RESTORE_NODE_KEY, defaultvalue)
        return if (restoreSceneId == defaultvalue)
            null
        else
            restoreSceneId
    }

    private fun getSceneRestore(context: Context) : String {
        val restoreId = checkForSceneRestore(context)
        if (restoreId == null) {
            val newKey = scenesRef.push().key!!
            saveSceneRestore(newKey, context)
            return newKey
        } else {
            return restoreId
        }
    }

    private fun saveSceneRestore(restoreId: String, context: Context) {
        val sharedPref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(RESTORE_NODE_KEY, restoreId)
            commit()
        }
    }

    fun checkForActiveScene(context: Context) : String? {
        val sharedPref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
        val defaultvalue = "empty"
        val activeSceneId = sharedPref.getString(CURRENT_ACTIVE_SCENE_KEY, defaultvalue)
        return if (activeSceneId == "empty")
            null
        else
            activeSceneId
    }

    private fun saveActiveScene(context: Context, sceneId: String) {
        val sharedPref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(CURRENT_ACTIVE_SCENE_KEY, sceneId)
            commit()
        }
    }

}