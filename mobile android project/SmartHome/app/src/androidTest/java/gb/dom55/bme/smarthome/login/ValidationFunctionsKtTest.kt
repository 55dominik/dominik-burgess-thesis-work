package gb.dom55.bme.smarthome.login

import org.junit.Test

import org.junit.Assert.*

class EmailTests {

    @Test
    fun validEmailTest() {
        assertTrue(isValidEmail("example@example.com"))
        assertFalse(isValidEmail(""))
        assertFalse(isValidEmail("hello@.com"))
        assertFalse(isValidEmail("random_username"))
    }

}