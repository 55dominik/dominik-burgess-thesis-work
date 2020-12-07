package gb.dom55.bme.smarthomepicontroller.model


//ONCE VALUES ARE WRITTEN HERE, DO NOT CHANGE THEM
//NUMBERS MUST BE UNIQUE (use unit test to verify)
enum class DeviceType(val type: Int) {

    //
    LIGHT(1),
    KETTLE(10),
    SOCKET(11),
    AIR_CON(12),
    DOOR_LOCK(13),

    RGBLIGHT(2),
    IR_REMOTE(20),
    RGB_IR_REMOTE(200),
    TV_REMOTE(201),
    COFFEE(21),
    THERMOSTAT(22),

    DIMMABLE_LIGHT(3),
    BLINDS(30),
    RADIATOR(31),

    //
    BOOL_SENSOR(51),
    DOOR_SENSOR(510),
    MOTION_SENSOR(511),
    WATER_PRESENCE_SENSOR(512),

    INT_SENSOR(52),
    CELSIUS_SENSOR(520),
    HUMIDITY_SENSOR(521),
    WATER_LEVEL_SENSOR(522),
    MOISTURE_SENSOR(523),

    NULL_DEVICE(10000)
}