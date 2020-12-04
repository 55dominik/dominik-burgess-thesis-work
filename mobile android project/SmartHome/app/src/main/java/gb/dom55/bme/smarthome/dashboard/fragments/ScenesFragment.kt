package gb.dom55.bme.smarthome.dashboard.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import gb.dom55.bme.smarthome.dashboard.adapters.touchhelpers.SceneTouchHelperCallback
import gb.dom55.bme.smarthome.dashboard.listeners.ScenesItemTouchListener
import gb.dom55.bme.smarthome.dashboard.adapters.ScenesAdapter
import gb.dom55.bme.smarthome.model.scenes.SceneItem
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.model.scenes.SceneManager
import gb.dom55.bme.smarthome.dashboard.dialogs.SceneDialogFragment
import gb.dom55.bme.smarthome.dashboard.DashboardViewModel
import kotlinx.android.synthetic.main.fragment_scenes.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScenesFragment : Fragment(), ScenesItemTouchListener, SceneDialogFragment.NewSceneDialogListener {

    val model: DashboardViewModel by activityViewModels()
    private lateinit var sceneAdapter: ScenesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_scenes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scenesFab.setOnClickListener {
            showEditSceneDialog(null)
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        scenesRecycler.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        sceneAdapter = ScenesAdapter(requireContext())
        sceneAdapter.clickListener = this
        model.sceneIds.observe(viewLifecycleOwner, Observer<MutableList<SceneItem>> { list ->

            sceneAdapter.replaceScenes(list)
            scenesRecycler.scrollToPosition(0)
        })
        scenesRecycler.adapter = sceneAdapter

        val callbackScene = SceneTouchHelperCallback(sceneAdapter)
        val touchHelperScene = ItemTouchHelper(callbackScene)
        touchHelperScene.attachToRecyclerView(scenesRecycler)

        scenesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !scenesFab.isShown)
                    scenesFab.show()
                else if(dy > 0 && scenesFab.isShown)
                    scenesFab.hide()
            }
        })
    }

    override fun onSceneSelected(scene: SceneItem) {
        SceneManager.activateScene(scene, requireContext())
    }

    override fun onSceneDeselected(scene: SceneItem) {
        SceneManager.deactivateScene(scene, requireContext())
    }

    override fun onSceneEdit(scene: SceneItem) {
        showEditSceneDialog(scene)
    }

    override fun onSceneReorder(scenes: MutableList<SceneItem>) {
        model.persistNewSceneOrder(scenes)
    }

    override fun onActiveSceneChange(scenes: MutableList<SceneItem>) {
        model.updateScenes(scenes)
    }

    override fun onSceneDeleted(scene: SceneItem) {
        GlobalScope.launch(Dispatchers.Main) {
            GlobalScope.async(Dispatchers.IO) { model.deleteScene(scene) }.await()
            val database = FirebaseDatabase.getInstance().reference
            database.child("scenes").child(scene.sceneId).removeValue()
            database.child("users").child(scene.userId)
                    .child("scenes").child(scene.sceneId).removeValue()
        }

    }

    private fun showEditSceneDialog(scene: SceneItem?) {
        val dialog = SceneDialogFragment(this, model.devices.value!!, scene)
        dialog.show(activity?.supportFragmentManager!!, "NewSceneDialogFragment")
    }

    override fun onDialogCreateClick(dialog: DialogFragment, scene: SceneItem) {
        GlobalScope.launch (Dispatchers.IO) {
            model.insertScene(scene)
        }
        dialog.dismiss()
    }

    override fun onDialogUpdateClick(dialog: DialogFragment, scene: SceneItem) {
        GlobalScope.launch(Dispatchers.IO) {
            model.updateSceneName(scene)
        }
        sceneAdapter.updateScene(scene)
        dialog.dismiss()
    }

}
