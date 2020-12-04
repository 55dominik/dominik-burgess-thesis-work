package gb.dom55.bme.smarthomepicontroller

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.firebase.auth.*
import com.google.firebase.database.*
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import javax.crypto.Cipher


class LoginManager(context: Context, val listener: LoggedInListener) {

    private var client = Nearby.getConnectionsClient(context)

    private var lifeCycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpoint: String, connectionInfo: ConnectionInfo) {
            Log.i(TAG, "Connection initiated. Endpoint [$endpoint]")
            payloadCallback.let { payloadCallback ->
                client.acceptConnection(endpoint, payloadCallback)
                client.stopAdvertising()
            }
        }

        override fun onConnectionResult(endpoint: String, connectionResolution: ConnectionResolution) {
            Log.i(TAG, "Connection result. Endpoint [$endpoint]")
        }

        override fun onDisconnected(endpoint: String) {
            Log.i(TAG, "Disconnected. Endpoint [$endpoint]")
        }
    }

    private var payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpoint: String, payload: Payload) {
            Log.d(TAG, "Payload received")

            val payloadBytes = payload.asBytes()!!
            val message = String(payloadBytes)

            if (message.contains(PUBLIC_KEY_REQUEST)) {
                sendPublicKey(endpoint)
            } else {
                val credentials = decryptPayload(message, keyPair?.private)
                client.disconnectFromEndpoint(endpoint)
                signInWithCredentials(credentials)
            }
        }
        override fun onPayloadTransferUpdate(endpoint: String, transferUpdate: PayloadTransferUpdate) {}
    }

    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var keyPair: KeyPair? = null

    init {
        if (auth.currentUser == null) {
            startLoginProcess()
        } else {
            checkLogoutRequested()
        }
    }

    private fun checkLogoutRequested() {
        val user = auth.currentUser!!

        database.child("users")
            .child(user.uid)
            .child("piConnected")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val piConnected = snapshot.getValue(Boolean::class.java)!!
                    if (piConnected) {
                        Log.d(TAG, "Already logged in")
                        listener.loginSuccess()
                    } else {
                        auth.signOut()
                        startLoginProcess()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    auth.signOut()
                    startLoginProcess()
                }
            })
    }

    @Suppress("DEPRECATION")
    private fun startLoginProcess() {
        keyPair = createAsymmetricKeyPair()

        client.startAdvertising(
            "Homeberry controller",
            SERVICE_ID,
            lifeCycleCallback,
            AdvertisingOptions(Strategy.P2P_STAR)
        ).addOnSuccessListener { Log.d(TAG, "Advertising successful") }
         .addOnFailureListener { exception -> exception.printStackTrace() }
    }

    private fun signInWithCredentials(credentials: String) {
        val email = credentials.substring(0, credentials.indexOfFirst { it == ' ' })
        val password = credentials.substring(credentials.indexOfFirst { it == ' ' }+1)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = auth.currentUser!!
                database.child("users").child(user.uid)
                    .child("piConnected").setValue(true)
                    .addOnSuccessListener {
                        listener.loginSuccess()
                    }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun createAsymmetricKeyPair(): KeyPair {

        val generator: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA
        )
        val builder = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_ECB)
         .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)

        generator.initialize(builder.build())
        return generator.genKeyPair()
    }

    private fun sendPublicKey(endpoint: String) {
        val publicKey = keyPair?.public
        val publicKeyString = Base64.encodeToString(publicKey?.encoded, 2)
        val publicKeyMessageString = "$PUBLIC_KEY_HEADER:$publicKeyString"
        val publicKeyPayload = Payload.fromBytes(publicKeyMessageString.toByteArray())
        client.sendPayload(endpoint, publicKeyPayload)
    }

    private fun decryptPayload(data: String, privateKey: PrivateKey?): String {
        val cipher: Cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val encryptedData = Base64.decode(data, Base64.DEFAULT)
        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData)
    }

    companion object {
        private const val SERVICE_ID = "gb.dom55.bme.smarthomepicontroller"
        private const val TAG = "LoginManager"
        private const val KEY_ALIAS = "KEY_ALIAS"
        private const val PUBLIC_KEY_REQUEST = "REQUEST_PUBLIC_KEY"
        private const val PUBLIC_KEY_HEADER = "PUBLIC_KEY"
        private const val CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
    }
}