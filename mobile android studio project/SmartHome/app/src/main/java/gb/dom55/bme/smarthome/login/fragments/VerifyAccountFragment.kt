package gb.dom55.bme.smarthome.login.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import gb.dom55.bme.smarthome.R
import gb.dom55.bme.smarthome.login.LogonActivity
import gb.dom55.bme.smarthome.login.LogonViewModel
import kotlinx.android.synthetic.main.fragment_verify_account.*
import java.lang.Exception

class VerifyAccountFragment : Fragment() {

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


        return inflater.inflate(R.layout.fragment_verify_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verifySendEmailAgain.setOnClickListener {
            user.sendEmailVerification().addOnSuccessListener {
                Toast.makeText(requireContext(), getString(R.string.email_resent_verification), Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), getString(R.string.failed_to_send_verification_email), Toast.LENGTH_LONG).show()
            }
        }

        verifyConfirmButton.setOnClickListener {
            if (model.email.value == null) {
                model.setEmail("failed")
                return@setOnClickListener
            }
            if (model.password.value == null) {
                model.setPassword("failed")
                return@setOnClickListener
            }

            val credential = EmailAuthProvider.getCredential(model.email.value!!, model.password.value!!)
            user.reauthenticate(credential)
                    .addOnSuccessListener {
                        user = auth.currentUser!!
                        checkAccountVerified()
                    }
                    .addOnFailureListener { exception ->
                        authFailure(exception)
                    }
        }

        verifyDifferentAccount.setOnClickListener {
            model.email.value = ""
            model.password.value = ""
            hostActivity.updateUI(LogonActivity.LOGIN)
        }
    }


    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            hostActivity.updateUI(LogonActivity.LOGIN)
        } else {
            user = auth.currentUser!!
            user.sendEmailVerification()
        }

    }

    private fun checkAccountVerified() {
        if (user.isEmailVerified) {
            database.child("users").child(user.uid).child("emailVerified").setValue(true)
            hostActivity.navigateToDashboard()
        } else {
            Toast.makeText(requireContext(), getString(R.string.email_is_not_verified), Toast.LENGTH_LONG).show()
        }
    }

    private fun authFailure(exception: Exception) {
        exception.printStackTrace()
    }


}
