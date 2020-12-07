package gb.dom55.bme.smarthome

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class SharedPrefHelperKtTest {

    lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun darkModeSharedPrefTest() {
        setDarkModeSharedPref(instrumentationContext, true)
        assertTrue(getDarkModeSharedPref(instrumentationContext))

        setDarkModeSharedPref(instrumentationContext, false)
        assertFalse(getDarkModeSharedPref(instrumentationContext))
    }


    @Test
    fun notificationSharedPref() {
        setNotificationSharedPref(instrumentationContext, true)
        assertTrue(getNotificationSharedPref(instrumentationContext))

        setNotificationSharedPref(instrumentationContext, false)
        assertFalse(getNotificationSharedPref(instrumentationContext))
    }

}