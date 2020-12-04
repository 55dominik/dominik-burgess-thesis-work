package gb.dom55.bme.smarthome.dashboard.adapters

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import gb.dom55.bme.smarthome.dashboard.adapters.touchhelpers.RecyclerViewTouchHelperInterface
import gb.dom55.bme.smarthome.model.scenes.SceneItem
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.listeners.ScenesItemTouchListener

class ScenesAdapter(val context: Context) : RecyclerView.Adapter<ScenesAdapter.SceneViewHolder>(), RecyclerViewTouchHelperInterface {

    private var scenes = mutableListOf<SceneItem>()
    var clickListener: ScenesItemTouchListener? = null

    private var checkedItemPosition = -1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SceneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scene, parent, false)
        return SceneViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int {
        return scenes.size
    }

    override fun onBindViewHolder(holder: SceneViewHolder, position: Int) {
        val scene = scenes[position]
        val color = TypedValue()
        context.theme.resolveAttribute(R.attr.itemColor, color, true)
        holder.sceneName.setText(scene.name)
        holder.editButton.setOnClickListener {
            clickListener?.onSceneEdit(scene)
        }
        if (scene.isActive) {
            holder.container.setCardBackgroundColor(Color.CYAN)
        } else {
            holder.container.setCardBackgroundColor(color.data)
        }
        holder.container.setOnClickListener {
            checkedItemPosition = position

            val newActiveState = !scene.isActive

            for (s in scenes) {
                if (s.isActive) {
                    s.isActive = false
                }
            }

            scene.isActive = newActiveState
            if (!newActiveState) {
                checkedItemPosition = -1

                clickListener?.onSceneDeselected(scene)

            } else {

                clickListener?.onSceneSelected(scene)

            }

            clickListener?.onActiveSceneChange(scenes)
            notifyDataSetChanged()
        }

    }

    fun addScene(added: SceneItem) {
        val position = scenes.size
        scenes.add(position, added)
        notifyItemInserted(position)
    }

    fun addMultipleScenes(added: MutableList<SceneItem>){
        scenes.addAll(added)
        scenes.sortBy { sceneItem -> sceneItem.positionInRV }
        notifyDataSetChanged()
    }

    fun replaceScenes(newScenes:  MutableList<SceneItem>) {
        scenes = newScenes
        scenes.sortBy { sceneItem -> sceneItem.positionInRV }
        notifyDataSetChanged()
    }

    fun updateScene(updated: SceneItem) {
        scenes.find { it.sceneId == updated.sceneId }?.name = updated.name
        notifyDataSetChanged()
    }

    override fun onItemDismissed(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.delete))
                .setMessage(context.getString(R.string.are_you_sure_delete_scene))
                .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                    val tmp = scenes.removeAt(position)
                    clickListener?.onSceneDeleted(tmp)
                    notifyItemRemoved(position)
                }
                .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                    notifyDataSetChanged()
                }
        builder.create().show()
    }

    class SceneViewHolder(itemView: View, var listener: ScenesItemTouchListener?)
        : RecyclerView.ViewHolder(itemView) {
        var sceneName: TextView = itemView.findViewById(R.id.sceneItemName)
        var editButton: ImageView = itemView.findViewById(R.id.sceneItemEditButton)
        var container: CardView = itemView.findViewById(R.id.sceneItemContainer)
    }
}