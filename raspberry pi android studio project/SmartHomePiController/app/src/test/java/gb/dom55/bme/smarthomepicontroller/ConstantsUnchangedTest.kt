package gb.dom55.bme.smarthomepicontroller

import org.junit.Assert.*
import org.junit.Test

class ConstantsUnchangedTest {

    @Test
    fun constantsTest() {
        assertEquals(5005, UDP_PORT)
        assertEquals(5006, TCP_PORT)
        assertEquals(1023, UDP_PACKET_SIZE)
    }
}
