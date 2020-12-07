package gb.dom55.bme.smarthome.login.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.login.isValidEmail
import gb.dom55.bme.smarthome.login.LogonActivity
import gb.dom55.bme.smarthome.login.LogonViewModel
import kotlinx.android.synthetic.main.fragment_forgot_password.*

import java.lang.Exception


class ForgotPasswordFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var hostActivity: LogonActivity
    private val model: LogonViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        hostActivity = activity as LogonActivity

        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forgotLinkToLogin.setOnClickListener {
            // Go to login fragment
            hostActivity.updateUI(LogonActivity.LOGIN)
        }

        forgotConfirmButton.setOnClickListener {
            model.password.value = ""
            resetPassword()
        }

        updateFromViewModel()
    }

    private fun updateFromViewModel() {
        model.email.value?.let {
            forgotEmailEdit.setText(model.email.value)
        }

        forgotEmailEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                model.setEmail(s.toString())
            }
        })

    }

    private fun resetPassword() {

        val email = forgotEmailEdit.text.toString()
        if (isValidEmail(email)) {
            auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            forgotEmailInfoText.text = getString(R.string.reset_link_sent_to, email)
                            forgotEmailInfoText.visibility = View.VISIBLE
                        } else {
                            resetFailed(task.exception)
                        }

                    }
        } else {
            forgotEmailEdit.error = getString(R.string.invalid_email_address)
            forgotEmailEdit.requestFocus()

        }
    }

    private fun resetFailed(exception: Exception?) {
        if (exception is FirebaseAuthInvalidUserException) {
            forgotEmailEdit.error = getString(R.string.no_account_of_email)
            forgotEmailEdit.requestFocus()
        }
        if (exception is FirebaseAuthInvalidCredentialsException) {
            forgotEmailEdit.error = getString(R.string.invalid_email_address)
            forgotEmailEdit.requestFocus()
        }
    }

}
