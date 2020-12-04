package gb.dom55.bme.smarthome.dashboard.listeners


import gb.dom55.bme.smarthome.model.scenes.SceneItem

interface ScenesItemTouchListener {
    fun onSceneSelected(scene: SceneItem)
    fun onSceneDeselected(scene: SceneItem)
    fun onSceneEdit(scene: SceneItem)
    fun onSceneReorder(scenes: MutableList<SceneItem>)
    fun onActiveSceneChange(scenes: MutableList<SceneItem>)
    fun onSceneDeleted(scene: SceneItem)
}