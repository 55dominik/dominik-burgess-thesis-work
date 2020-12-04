package gb.dom55.bme.smarthome.dashboard.listeners

import gb.dom55.bme.smarthome.model.devices.AbstractDevice

interface DeviceItemClickListener {
    fun onDeviceItemClick(deviceData: AbstractDevice)
    fun onDeviceDeleted(deviceData: AbstractDevice)
}