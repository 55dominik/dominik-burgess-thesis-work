package gb.dom55.bme.smarthome.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.dashboard.DashboardActivity
import gb.dom55.bme.smarthome.login.fragments.ForgotPasswordFragment
import gb.dom55.bme.smarthome.login.fragments.LoginFragment
import gb.dom55.bme.smarthome.login.fragments.RegisterFragment
import gb.dom55.bme.smarthome.login.fragments.VerifyAccountFragment
import gb.dom55.bme.smarthome.getDarkModeSharedPref
import gb.dom55.bme.smarthome.nearby.NearbyConnectionLoginActivity
import kotlinx.android.synthetic.main.activity_logon.*

class LogonActivity : AppCompatActivity() {

    companion object {
        const val LOGIN = 0
        const val SIGNUP = 1
        const val FORGOT = 2
        const val VERIFY = 3
    }

    private lateinit var auth : FirebaseAuth
    private lateinit var user : FirebaseUser
    private lateinit var database: DatabaseReference

    private lateinit var circularProgressDrawable: CircularProgressDrawable
    val model: LogonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(if (getDarkModeSharedPref(this)){
            R.style.DarkTheme
        } else {
            R.style.LightTheme
        })
        setContentView(R.layout.activity_logon)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        circularProgressDrawable = CircularProgressDrawable(this)

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        if (currentUser != null && currentUser.isEmailVerified) {
            showProgressRing()
            navigateToDashboard()
        } else {
            updateUI(LOGIN)
        }
    }


    fun navigateToDashboard() {

        user = auth.currentUser!!
        database.child("users").child(user.uid).child("piConnected")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value == null) {
                            updateUI(LOGIN)
                            return
                        }

                        val piConnected = snapshot.value as Boolean
                        if (piConnected) {
                            val intent = Intent(this@LogonActivity, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            if (model.email.value == null || model.password.value == null) {
                                hideProgressRing()
                                updateUI(LOGIN)
                                return
                            } else {
                                val intent = Intent(this@LogonActivity, NearbyConnectionLoginActivity::class.java)
                                intent.putExtra("email", model.email.value)
                                intent.putExtra("password", model.password.value)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }

                })
    }

    fun updateUI(fragmentId: Int) {
        var fragment = Fragment()
        when(fragmentId) {
            LOGIN ->  fragment = LoginFragment()
            FORGOT -> fragment = ForgotPasswordFragment()
            SIGNUP -> fragment = RegisterFragment()
            VERIFY -> fragment = VerifyAccountFragment()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.logonContentFrame, fragment)
                .addToBackStack(null)
                .commit()
        supportFragmentManager.executePendingTransactions()
    }

    private fun showProgressRing() {

        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f

        circularProgressDrawable.start()
        circularProgress.setImageDrawable(circularProgressDrawable)
    }

    private fun hideProgressRing() {
        circularProgressDrawable.stop()
    }




}
