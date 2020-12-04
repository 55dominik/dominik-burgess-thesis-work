package gb.dom55.bme.smarthome.dashboard.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.model.scenes.SceneItem
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.model.devices.AbstractDevice
import kotlinx.android.synthetic.main.dialog_fragment_new_scene.view.*
import java.lang.IllegalStateException

class SceneDialogFragment(var listener: NewSceneDialogListener,
                          val allDevices: MutableList<AbstractDevice>,
                          private val scene: SceneItem? = null) : DialogFragment() {
    private val TAG = "SceneDialog"

    private lateinit var nameEditText: EditText
    private lateinit var devicesRV: RecyclerView
    private var includedDevices = mutableListOf<AbstractDevice>()

    interface NewSceneDialogListener {
        fun onDialogCreateClick(dialog: DialogFragment, scene: SceneItem)
        fun onDialogUpdateClick(dialog: DialogFragment, scene: SceneItem)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = requireActivity().layoutInflater

            val confirmButton = if (scene == null) { "Create" } else {"Save changes"}

            builder.setView(getContentView(inflater))
                    .setPositiveButton(confirmButton) { _, _ ->
                        if (scene == null) {
                            listener.onDialogCreateClick(this, generateSceneData())
                        } else {
                            listener.onDialogUpdateClick(this, updateSceneData())
                        }
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        dismiss()
                    }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun getContentView(inflater: LayoutInflater): View {
        val contentView = inflater.inflate(R.layout.dialog_fragment_new_scene, null)
        nameEditText = contentView.addSceneNameField
        devicesRV = contentView.addSceneRecyclerView

        devicesRV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        devicesRV.adapter = NewSceneDeviceSelectionAdapter()
        scene?.let {
            nameEditText.setText(it.name)
            getDevicesInSceneAndPrepareDataForRecyclerView(it.sceneId)

        }

        return contentView

    }

    private fun getDevicesInSceneAndPrepareDataForRecyclerView(sceneId: String) {
        val sceneRef = FirebaseDatabase.getInstance().reference
                .child("scenes").child(sceneId)
                .child("deviceIds")

        sceneRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (idSnapshot in snapshot.children) {
                    val included = allDevices.find { device ->
                        device.deviceid == idSnapshot.getValue(String::class.java)!!
                    }
                    included?.let {
                        if (!includedDevices.contains(it))
                            includedDevices.add(it)
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })
    }


    private fun generateSceneData(): SceneItem {
        val sceneID = FirebaseDatabase.getInstance().reference.child("scenes").push().key!!
        val userID = FirebaseAuth.getInstance().currentUser?.uid!!
        val scene = SceneItem(sceneID, userID, nameEditText.text.toString(), false)

        val sceneRef = FirebaseDatabase.getInstance().reference
                .child("scenes").child(sceneID)
        val devicesRef = FirebaseDatabase.getInstance().reference
                .child("devices")

        sceneRef.child("sceneData").setValue(scene)
        FirebaseDatabase.getInstance().reference.child("users").child(userID).child("scenes").child(sceneID).setValue(true)

        writeDevicesUnderScene(devicesRef, sceneRef)

        return scene
    }

    private fun updateSceneData(): SceneItem {
        scene?.let {
            // Explanation for "false" isActive :
            // New scene must be false since devices added might have changed, easier to turn it off
            val updatedScene = SceneItem(it.sceneId, it.userId, nameEditText.text.toString(), false)

            val sceneRef = FirebaseDatabase.getInstance().reference
                    .child("scenes").child(updatedScene.sceneId)
            val devicesRef = FirebaseDatabase.getInstance().reference
                    .child("devices")

            sceneRef.child("sceneData").setValue(updatedScene)

           sceneRef.child("devices").removeValue()
                    .addOnCompleteListener {
                        sceneRef.child("deviceIds").removeValue()
                                .addOnCompleteListener {
                                    writeDevicesUnderScene(devicesRef, sceneRef)
                               }
                    }
            return updatedScene

        }
        // This should never happen, updating can only happen on existing SceneItems.
        return SceneItem("error", "error", "error")
    }

    private fun writeDevicesUnderScene(devicesRef: DatabaseReference, sceneRef: DatabaseReference) {
        val deviceIdStrings = mutableListOf<String>()
        for (device in includedDevices) {
            copyFirebaseDeviceNode(
                    fromPath = devicesRef.child(device.deviceid),
                    toPath = sceneRef.child("devices").child(device.deviceid))
            deviceIdStrings.add(device.deviceid)

            Log.d(TAG, device.name+"written to devices in firebase")
        }
        sceneRef.child("deviceIds").setValue(deviceIdStrings)
    }

    private fun copyFirebaseDeviceNode(fromPath: DatabaseReference, toPath: DatabaseReference) {
        val valueEventListener = object:  ValueEventListener {
            override fun onDataChange(snaphsot: DataSnapshot) {
                toPath.setValue(snaphsot.getValue()).addOnCompleteListener {
                    if (it.isComplete) {
                        Log.d(TAG, "Scene copy successful")
                    } else {
                        Log.d(TAG, "Scene copy failed")
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        }
        fromPath.addListenerForSingleValueEvent(valueEventListener)
    }

    inner class NewSceneDeviceSelectionAdapter: RecyclerView.Adapter<NewSceneDeviceSelectionAdapter.DeviceCheckboxViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceCheckboxViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_new_scene_device_checkbox, parent, false)
            return DeviceCheckboxViewHolder(view)
        }

        override fun getItemCount(): Int = allDevices.size

        override fun onBindViewHolder(holder: DeviceCheckboxViewHolder, position: Int) {
            val device = allDevices[position]
            holder.deviceCheckBox.setText(device.name)
            if (includedDevices.contains(device)) {
                holder.deviceCheckBox.isChecked = true
            }

            holder.deviceCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!includedDevices.contains(device))
                        includedDevices.add(device)
                    Log.d(TAG, device.name+"added to list")
                } else {
                    includedDevices.remove(device)
                    Log.d(TAG, device.name+"removed from list")
                }
            }
        }


        inner class DeviceCheckboxViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            var deviceCheckBox : CheckBox = itemView.findViewById(R.id.addSceneDeviceItemCheckbox)
        }
    }

}