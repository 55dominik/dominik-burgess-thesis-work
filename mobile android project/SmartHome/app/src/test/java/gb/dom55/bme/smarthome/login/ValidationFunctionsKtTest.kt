package gb.dom55.bme.smarthome.login

import org.junit.Test

import org.junit.Assert.*

class PasswordTests {

    @Test
    fun shortPasswordTest() {
        assertEquals(ValidationError.PASSWORD_TOO_SHORT, isValidPassword(""))
        assertEquals(ValidationError.PASSWORD_TOO_SHORT, isValidPassword("123456789"))
        assertEquals(ValidationError.PASSWORD_TOO_SHORT, isValidPassword("abcABC"))
    }

    @Test
    fun noUppercasePasswordTest() {
        assertEquals(ValidationError.PASSWORD_NO_UPPER, isValidPassword("01234567890"))
        assertEquals(ValidationError.PASSWORD_NO_UPPER, isValidPassword("abcdefghijk"))
        assertEquals(ValidationError.PASSWORD_NO_UPPER, isValidPassword("abcdefg12345"))
    }

    @Test
    fun noLowercasePasswordTest() {
        assertEquals(ValidationError.PASSWORD_NO_LOWER, isValidPassword("0123456789A"))
        assertEquals(ValidationError.PASSWORD_NO_LOWER, isValidPassword("ABCDEFGHIKJLMNOP"))
        assertEquals(ValidationError.PASSWORD_NO_LOWER, isValidPassword("ABCDEFGHI123456"))
    }

    @Test
    fun commonPasswordTest() {
        // Dragging finger across first row on keyboard
        assertEquals(ValidationError.PASSWORD_TOO_COMMON, isValidPassword("Qwertyuiop"))
        assertEquals(ValidationError.PASSWORD_TOO_COMMON, isValidPassword("Qwertzuiop"))
    }

    @Test
    fun validPasswordTest() {
        assertEquals(ValidationError.PASSWORD_OK, isValidPassword("YqS87Dq\"K>[[y#"))
        assertEquals(ValidationError.PASSWORD_OK, isValidPassword("icorxlOSrUFelOL"))

    }

}