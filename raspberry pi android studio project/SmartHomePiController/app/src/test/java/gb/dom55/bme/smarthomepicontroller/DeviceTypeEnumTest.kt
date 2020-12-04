package gb.dom55.bme.smarthomepicontroller

import gb.dom55.bme.smarthomepicontroller.model.DeviceType
import org.junit.Test

class DeviceTypeEnumTest {
    @Test
    fun deviceTypeEnumValidator() {
        var consistent = true
        for (currentType in DeviceType.values()) {
            val currentNumber = currentType.type
            for (checkType in DeviceType.values()) {
                val checkNumber = checkType.type
                if (currentType != checkType && currentNumber == checkNumber) {
                    consistent = false
                    break
                }
            }
        }
        assert(consistent)
    }
}