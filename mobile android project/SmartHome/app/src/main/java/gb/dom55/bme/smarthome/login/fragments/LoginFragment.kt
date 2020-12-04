package gb.dom55.bme.smarthome.login.fragments

import android.app.Activity
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.login.ValidationError
import gb.dom55.bme.smarthome.login.isValidEmail
import gb.dom55.bme.smarthome.login.isValidPassword
import gb.dom55.bme.smarthome.login.LogonActivity
import gb.dom55.bme.smarthome.login.LogonViewModel
import kotlinx.android.synthetic.main.fragment_login.*
import java.lang.Exception

class LoginFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var user : FirebaseUser
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

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stringBuilder = SpannableStringBuilder()
        val str1 = SpannableString(getString(R.string.dont_have_an_account_question)+" ")
        stringBuilder.append(str1)
        val str2 = SpannableString(getString(R.string.sign_up))
        str2.setSpan(ForegroundColorSpan(resources.getColor(R.color.lightBlue)), 0, str2.length, 0)
        stringBuilder.append(str2)
        loginSignUpLink.setText(stringBuilder, TextView.BufferType.SPANNABLE)


        loginSignUpLink.setOnClickListener {
            hostActivity.updateUI(LogonActivity.SIGNUP)
        }

        loginForgotPasswordLink.setOnClickListener {
            hostActivity.updateUI(LogonActivity.FORGOT)
        }

        loginButton.setOnClickListener {
            signInUserWithEmailAndPassword()
        }

        updateFromViewModel()
    }

    private fun updateFromViewModel() {
        model.email.value?.let {
            loginEmailEdit.setText(model.email.value)
        }
        model.password.value?.let {
            loginPasswordEdit.setText(model.password.value)
        }

        loginEmailEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                model.setEmail(s.toString())
            }
        })

        loginPasswordEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                model.setPassword(s.toString())
            }
        })

    }


    private fun signInUserWithEmailAndPassword() {
        val email = loginEmailEdit.text.toString()
        val password = loginPasswordEdit.text.toString()

        if (!isValidEmail(email)) {
            loginEmailEdit.error = getString(R.string.invalid_email_address)
            loginEmailEdit.requestFocus()
            return
        }

        when(isValidPassword(password)) {

            ValidationError.PASSWORD_TOO_SHORT -> {
                loginPasswordEdit.error = getString(R.string.password_at_least__num_char_error)
                loginPasswordEdit.requestFocus()
                return
            }
            ValidationError.PASSWORD_NO_UPPER -> {
                loginPasswordEdit.error = getString(R.string.password_have_uppercase_error)
                loginPasswordEdit.requestFocus()
                return
            }
            ValidationError.PASSWORD_NO_LOWER -> {
                loginPasswordEdit.error = getString(R.string.password_have_lowercase_error)
                loginPasswordEdit.requestFocus()
                return
            }
            ValidationError.PASSWORD_NO_NUMBER -> {
                // Not required
            }
            ValidationError.PASSWORD_TOO_COMMON -> {
                // Not required for login
            }
            ValidationError.PASSWORD_OK -> {
                // Password OK, No operation
            }
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity as Activity) { task ->
                    if (task.isSuccessful) {
                        user = auth.currentUser!!
                        authSuccess()
                    } else {
                        authFailure(task.exception)
                    }
                }
    }

    private fun authSuccess() {
        if (user.isEmailVerified) {
            hostActivity.navigateToDashboard()
        } else {
            // Go to email verification fragment
            hostActivity.updateUI(LogonActivity.VERIFY)
        }
    }

    private fun authFailure(exception: Exception?) {
        exception?.printStackTrace()

        if (exception is FirebaseAuthInvalidCredentialsException) {
            loginPasswordEdit.error = getString(R.string.incorrect_password)
            loginPasswordEdit.setText("")
            loginPasswordEdit.requestFocus()
        }

        if (exception is FirebaseAuthInvalidUserException) {
            loginEmailEdit.error = getString(R.string.no_account_of_email)
            loginEmailEdit.requestFocus()
        }
    }




}
