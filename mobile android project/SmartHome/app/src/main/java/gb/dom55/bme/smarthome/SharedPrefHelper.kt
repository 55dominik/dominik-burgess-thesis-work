package gb.dom55.bme.smarthome

import android.content.Context
private const val KEY = "PREFERENCE_KEY"
private const val DARKMODE_KEY = "DARKMODE_PREF_KEY"
private const val NOTIFICATIONS_KEY = "NOTIFICATIONS_PREF_KEY"
fun setDarkModeSharedPref(context: Context, dark: Boolean) {
    val sharedPref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putBoolean(DARKMODE_KEY, dark)
        commit()
    }
}

fun getDarkModeSharedPref(context: Context) : Boolean {
    val sharedPref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
    val defaultValue = false
    return sharedPref.getBoolean(DARKMODE_KEY, defaultValue)
}

fun setNotificationSharedPref(context: Context, receive: Boolean) {
    val sharedPref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putBoolean(NOTIFICATIONS_KEY, receive)
        commit()
    }
}

fun getNotificationSharedPref(context: Context) : Boolean {
    val sharedPref = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
    val defaultValue = true
    return sharedPref.getBoolean(NOTIFICATIONS_KEY, defaultValue)
}

