package gb.dom55.bme.smarthome.login.fragments

import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.login.ValidationError
import gb.dom55.bme.smarthome.login.isValidEmail
import gb.dom55.bme.smarthome.login.isValidPassword
import gb.dom55.bme.smarthome.login.LogonActivity
import gb.dom55.bme.smarthome.login.LogonViewModel
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

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


        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val stringBuilder = SpannableStringBuilder()
        val str1 = SpannableString(getString(R.string.already_have_account_question)+" ")
        stringBuilder.append(str1)
        val str2 = SpannableString(getString(R.string.sign_in))
        str2.setSpan(ForegroundColorSpan(resources.getColor(R.color.lightBlue)), 0, str2.length, 0)
        stringBuilder.append(str2)
        regLinkToLogin.setText(stringBuilder, TextView.BufferType.SPANNABLE)

        regConfirmButton.setOnClickListener {
            registerUserWithEmailAndPassword()
        }

        regLinkToLogin.setOnClickListener {
            hostActivity.updateUI(LogonActivity.LOGIN)
        }

        updateFromViewModel()
    }

    private fun updateFromViewModel() {
        model.email.value?.let {
            regEmailEdittext.setText(model.email.value)
        }
        model.password.value?.let {
            regPasswordEdit.setText(model.password.value)
        }

        regEmailEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                model.setEmail(s.toString())
            }
        })

        regPasswordEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
                model.setPassword(s.toString())
            }
        })

    }

    private fun registerUserWithEmailAndPassword() {
        val email = regEmailEdittext.text.toString()
        val password = regPasswordEdit.text.toString()
        val confirm = regConfirmPasswordEdit.text.toString()

        if (!isValidEmail(email)) {
            regEmailEdittext.error = getString(R.string.invalid_email_address)
            regEmailEdittext.requestFocus()
            return
        }
        when(isValidPassword(password)) {

            ValidationError.PASSWORD_TOO_SHORT -> {
                regPasswordEdit.error = getString(R.string.password_at_least__num_char_error)
                regPasswordEdit.requestFocus()
            }

            ValidationError.PASSWORD_NO_UPPER -> {
                regPasswordEdit.error = getString(R.string.password_have_uppercase_error)
                regPasswordEdit.requestFocus()
                return
            }

            ValidationError.PASSWORD_NO_LOWER -> {
                regPasswordEdit.error = getString(R.string.password_have_lowercase_error)
                regPasswordEdit.requestFocus()
                return
            }

            ValidationError.PASSWORD_TOO_COMMON -> {
                regPasswordEdit.setError(getString(R.string.password_too_common))
                regPasswordEdit.requestFocus()
                return
            }
        }

        if (password != confirm) {
            regConfirmPasswordEdit.setError(getString(R.string.passwords_do_not_match))
            regConfirmPasswordEdit.requestFocus()
            return
        }

        registerUser(email, password)
    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        authSuccess()
                    } else {
                        authFailure(task.exception)
                    }
                }
    }

    private fun authFailure(exception: Exception?) {
        if (exception is FirebaseAuthUserCollisionException) {
            regEmailEdittext.setError(getString(R.string.email_already_exists))
            regEmailEdittext.requestFocus()
        }
        if (exception is FirebaseAuthInvalidCredentialsException) {
            regEmailEdittext.setError(getString(R.string.invalid_email_address))
            regEmailEdittext.requestFocus()
        }
    }

    private fun authSuccess() {
        // Add user to firebase realtime database then go to verify fragment
        user = auth.currentUser!!
        database.child("users").child(user.uid).child("email").setValue(user.email)
        database.child("users").child(user.uid).child("uid").setValue(user.uid)
        database.child("users").child(user.uid).child("email").setValue(user.email)
        database.child("users").child(user.uid).child("piConnected").setValue(false)
        hostActivity.updateUI(LogonActivity.VERIFY)
    }




}
