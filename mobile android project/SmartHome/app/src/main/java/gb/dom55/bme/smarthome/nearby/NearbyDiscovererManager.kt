package gb.dom55.bme.smarthome.nearby

import android.content.Context
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import gb.dom55.bme.smarthome.nearby.listeners.NearbyEventListener
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class NearbyDiscovererManager(var context: Context, val listener: NearbyEventListener) {

    private var client: ConnectionsClient = Nearby.getConnectionsClient(context)
    private var lifecycleCallback : ConnectionLifecycleCallback
    private var messageText: String = ""
    private var currentEndpoint: String = ""

    companion object {
        private const val TAG = "NearbyDiscovererManager"
        private const val SERVICE_ID = "gb.dom55.bme.smarthomepicontroller"
        private const val CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding"
        private const val KEY_FACTORY_ALGORITHM = "RSA"
        private const val PUBLIC_KEY_PREFIX = "PUBLIC_KEY:"
        private const val REQUEST_PUBLIC_KEY_MESSAGE = "REQUEST_PUBLIC_KEY"
    }

    init {
        val discoveryCallback = object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(s: String, endpointInfo: DiscoveredEndpointInfo) {
                listener.onDiscovered()
                getConnection(s)
            }

            override fun onEndpointLost(s: String) {}
        }

        client.startDiscovery(
                SERVICE_ID,
                discoveryCallback,
                DiscoveryOptions(Strategy.P2P_STAR)
        ).addOnSuccessListener {
            listener.startedDiscovering()
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            listener.onFailure()
        }

        lifecycleCallback = object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpoint: String, connectionInfo: ConnectionInfo) {
                currentEndpoint = endpoint
                client.acceptConnection(endpoint, payloadCallback)
            }

            override fun onConnectionResult(s: String, connectionResolution: ConnectionResolution) {
                when(connectionResolution.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> listener.onConnected()
                    ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> Log.d(TAG, "Connection rejected")
                    ConnectionsStatusCodes.STATUS_ERROR -> Log.d(TAG, "Connection error")
                }
            }

            override fun onDisconnected(s: String) {}
        }
    }
    private var payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpoint: String, payload: Payload) {
            Log.d(TAG, "Payload received")

            val payloadBytes = payload.asBytes()!!
            val message = String(payloadBytes)
            if (message.startsWith(PUBLIC_KEY_PREFIX)) {
                val publicKeyString = message.replaceFirst(PUBLIC_KEY_PREFIX, "")
                encryptAndSendLastMessage(publicKeyString)
            }
        }
        override fun onPayloadTransferUpdate(s: String, transferUpdate: PayloadTransferUpdate) {}
    }

    private fun encryptAndSendLastMessage(publicKeyString: String) {
        var publicKey: PublicKey? = null
        try {
            val data: ByteArray = Base64.decode(publicKeyString, Base64.DEFAULT)
            val spec = X509EncodedKeySpec(data)
            val fact = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM)
            publicKey = fact.generatePublic(spec)
        } catch (e: GeneralSecurityException) {
            e.printStackTrace();
        }

        val cipher: Cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val bytes = cipher.doFinal(messageText.toByteArray())
        val credentialsPayload = Payload.fromBytes(Base64.encodeToString(bytes, Base64.DEFAULT).toByteArray())

        if (currentEndpoint != "") {
            client.sendPayload(currentEndpoint, credentialsPayload)
            client.disconnectFromEndpoint(currentEndpoint)
        }
    }

    private fun getConnection(endpointId: String) {
        client.requestConnection(endpointId, endpointId, lifecycleCallback)
                .addOnSuccessListener {
                    Log.d(TAG, "Requesting connection")
                    client.stopDiscovery()
                }
                .addOnFailureListener {
                    Log.d(TAG, "Error requesting connection")
                    listener.onFailure()
                }
    }

    fun sendDataEncrypted(text: String) {
        messageText = text
        if (currentEndpoint != "") {
            Log.d(TAG, "Request public key")
            val payload = Payload.fromBytes(REQUEST_PUBLIC_KEY_MESSAGE.toByteArray())
            client.sendPayload(currentEndpoint, payload)
        }
    }



}