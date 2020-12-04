package gb.dom55.bme.smarthome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class NullActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        throw IllegalStateException("This activity must never start")
    }
}
