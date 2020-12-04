package gb.dom55.bme.smarthome.model.devices

//ONCE VALUES ARE WRITTEN HERE, DO NOT CHANGE THEM
//NUMBERS MUST BE UNIQUE (use unit test to verify)
enum class DeviceType(val type: Int, val hasDashView: Boolean, val hasSensorView: Boolean) {

    //
    LIGHT(1, true, false),
    KETTLE(10, true, false),
    SOCKET(11, true, false),
    AIR_CON(12, true, false),
    DOOR_LOCK(13, true, false),

    RGBLIGHT(2, true, false),
    IR_REMOTE(20, true, false),
    RGB_IR_REMOTE(200, true, false),
    TV_REMOTE(201, true, false),

    COFFEE(21, true, true),
    THERMOSTAT(22, true, true),

    DIMMABLE_LIGHT(3, true, false),
    BLINDS(30, true, false),
    RADIATOR(31, true, false),


    //
    BOOL_SENSOR(51, false, true),
    DOOR_SENSOR(510, false, true),
    MOTION_SENSOR(511, false, true),
    WATER_PRESENCE_SENSOR(512, false, true),

    INT_SENSOR(52, false, true),
    CELSIUS_SENSOR(520, false, true),
    HUMIDITY_SENSOR(521, false, true),
    WATER_LEVEL_SENSOR(522, false, true),
    MOISTURE_SENSOR(523, false, true),

    NULL_DEVICE(10000, false, false)
}