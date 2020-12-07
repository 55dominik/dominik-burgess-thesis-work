package gb.dom55.bme.smarthome.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogonViewModel : ViewModel() {

    val email : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val password : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun setEmail(s: String) {
        email.value = s
    }

    fun setPassword(s: String) {
        password.value = s
    }
    
}