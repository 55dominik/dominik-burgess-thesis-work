package gb.dom55.bme.smarthomepicontroller

import com.google.firebase.database.DataSnapshot
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import java.lang.Exception
import java.net.InetAddress

class DeviceManagerTest {

    @Test
    fun addDeviceFromUserNodeTest() {
        val snapshot: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(snapshot.child("id").exists()).thenReturn(true)
        `when`(snapshot.child("connection").exists()).thenReturn(true)
        `when`(snapshot.child("type").exists()).thenReturn(true)

        `when`(snapshot.child("id").getValue(String::class.java)).thenReturn("test")
        `when`(snapshot.child("connection").getValue(String::class.java)).thenReturn("t3sT")
        `when`(snapshot.child("type").getValue(Int::class.java)).thenReturn(1)

        DeviceManager.addDeviceFromUserNode(snapshot)

        assertEquals(1, DeviceManager.deviceCount())
    }

    @Test
    fun setDeviceIpAddressTest() {
        val snapshot: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(snapshot.child("id").exists()).thenReturn(true)
        `when`(snapshot.child("connection").exists()).thenReturn(true)
        `when`(snapshot.child("type").exists()).thenReturn(true)

        `when`(snapshot.child("id").getValue(String::class.java)).thenReturn("test")
        `when`(snapshot.child("connection").getValue(String::class.java)).thenReturn("t3sT")
        `when`(snapshot.child("type").getValue(Int::class.java)).thenReturn(1)

        DeviceManager.addDeviceFromUserNode(snapshot)

        assertTrue(
            DeviceManager.setDeviceIpAddress(
                InetAddress.getByName("192.168.1.32"),
                "t3st"
            )
        )

        assertFalse(
            DeviceManager.setDeviceIpAddress(
                InetAddress.getByName("192.168.1.64"),
                "doesNotExist"
            )
        )
    }

    @Test
    fun removeDevice() {
        val snapshot: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(snapshot.child("id").exists()).thenReturn(true)
        `when`(snapshot.child("connection").exists()).thenReturn(true)
        `when`(snapshot.child("type").exists()).thenReturn(true)

        `when`(snapshot.child("id").getValue(String::class.java)).thenReturn("test")
        `when`(snapshot.child("connection").getValue(String::class.java)).thenReturn("t3sT")
        `when`(snapshot.child("type").getValue(Int::class.java)).thenReturn(1)

        DeviceManager.addDeviceFromUserNode(snapshot)
        DeviceManager.removeDevice(snapshot)

        assertEquals(DeviceManager.deviceCount(), 0)
    }

    @Test
    @Throws(Exception::class)
    fun updateDeviceExceptionTest() {
        val snapshot: DataSnapshot = mock(DataSnapshot::class.java)
        `when`(snapshot.child("id").exists()).thenReturn(true)
        `when`(snapshot.child("connection").exists()).thenReturn(true)
        `when`(snapshot.child("type").exists()).thenReturn(true)

        `when`(snapshot.child("id").getValue(String::class.java)).thenReturn("test")
        `when`(snapshot.child("connection").getValue(String::class.java)).thenReturn("t3sT")
        `when`(snapshot.child("type").getValue(Int::class.java)).thenReturn(1)
        DeviceManager.addDeviceFromUserNode(snapshot)

        DeviceManager.updateDevice("t3st", "invalid json string")

    }


}