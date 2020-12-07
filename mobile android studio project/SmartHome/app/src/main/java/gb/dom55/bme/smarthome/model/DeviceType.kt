package gb.dom55.bme.smarthome.model

import gb.dom55.bme.smarthome.R

//ONCE VALUES ARE WRITTEN HERE, DO NOT CHANGE THEM
//NUMBERS MUST BE UNIQUE (use unit test to verify)
enum class DeviceType(
        val type: Int,
        val hasDashView: Boolean,
        val hasSensorView: Boolean,
        val nameResource: Int
) {

    //
    LIGHT(1, true, false, R.string.displayname_light),
    KETTLE(10, true, false, R.string.displayname_kettle),
    SOCKET(11, true, false, R.string.displayname_socket),
    AIR_CON(12, true, false, R.string.displayname_cooler),
    DOOR_LOCK(13, true, false, R.string.displayname_door_lock),

    RGBLIGHT(2, true, false, R.string.displayname_rgblight),
    IR_REMOTE(20, true, false, R.string.displayname_ir_remote),
    RGB_IR_REMOTE(200, true, false, R.string.displayname_ir_rgb_remote),
    TV_REMOTE(201, true, false, R.string.displayname_tv_remote),

    COFFEE(21, true, true, R.string.displayname_coffeemaker),
    THERMOSTAT(22, true, true, R.string.displayname_thermostat),

    DIMMABLE_LIGHT(3, true, false, R.string.displayname_dimmable_light),
    BLINDS(30, true, false, R.string.displayname_blinds),
    RADIATOR(31, true, false, R.string.displayname_heater),

    //
    BOOL_SENSOR(51, false, true, R.string.displayname_on_off_sensor),
    DOOR_SENSOR(510, false, true, R.string.displayname_door_sensor),
    MOTION_SENSOR(511, false, true, R.string.displayname_motion_sensor),
    WATER_PRESENCE_SENSOR(512, false, true, R.string.displayname_water_presence_sensor),

    INT_SENSOR(52, false, true, R.string.displayname_integer_sensor),
    CELSIUS_SENSOR(520, false, true, R.string.displayname_temperature_sensor),
    HUMIDITY_SENSOR(521, false, true, R.string.displayname_humidity),
    WATER_LEVEL_SENSOR(522, false, true, R.string.displayname_water_level_sensor),
    MOISTURE_SENSOR(523, false, true, R.string.displayname_moisture_sensor),

    NULL_DEVICE(10000, false, false, R.string.displayname_nulldevice)
}