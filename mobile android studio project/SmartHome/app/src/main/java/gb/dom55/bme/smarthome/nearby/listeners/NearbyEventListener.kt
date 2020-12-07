package gb.dom55.bme.smarthome.nearby.listeners

interface NearbyEventListener {
    fun onDiscovered()
    fun startedDiscovering()
    fun onConnected()
    fun onFailure()
}