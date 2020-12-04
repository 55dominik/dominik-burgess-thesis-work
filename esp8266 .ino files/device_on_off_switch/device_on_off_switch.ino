#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include <ArduinoJson.h>

//////////////////////////////////////////////
// USER DEFINED DATA:						//
// Please enter your WiFi access and		//
// device details here						//
//											//
// WiFi name:					       		//
const char* ssid = "Wifi name here";
//											//
// WiFi password:							//
const char* password = "Wifi password here";
//											//
// Your device's ID:						//
const char verification[] = "Code from app";
//											//
// If your device has sensors,				//
// change to true:							//
#define HAS_SENSORS false
//											//
//////////////////////////////////////////////

unsigned long currentMs;
unsigned long prevMs;
unsigned long SENSOR_UPDATE_TIME_MS = 5000;

const bool DEBUG = true;

#define TCP_PORT 5006
#define UDP_PORT 5005
#define REQUEST_FRIENDLY_ID "REQUEST_FRIENDLY_ID"
#define MESSAGE_RECEIVED "MESSAGE_RECEIVED"
#define MAX_SRV_CLIENTS 6
IPAddress raspberryIp;
IPAddress broadcastIp;

WiFiServer server(TCP_PORT);
WiFiClient serverClients[MAX_SRV_CLIENTS];
WiFiUDP Udp;

unsigned const long UDP_BROADCAST_TIMEOUT_MS = 1000;
const int UDP_PACKET_SIZE = 1023;
byte udpPacketBuffer[UDP_PACKET_SIZE] = "0";

void setup()
{
	if (DEBUG) Serial.begin(115200);
	setupWiFi();
	Serial.println("Starting UDP exchange...");
	setupIpExchange();
	Serial.println("Starting TCP server...");
	setupTcpServer();
	setupTimer();
}

void loop()
{
	listenForData();
	
	currentMs = millis();
	if (currentMs - prevMs > SENSOR_UPDATE_TIME_MS) {
		prevMs = currentMs;
		if (HAS_SENSORS){
			updateData();
		}
	}	
}

class Device {
	private:
		// Define Pins here
		int signalPin = D2;
	public:
		Device() {
			pinMode(signalPin, OUTPUT);
		}
		// Parse Json and process received data
		// Use https://arduinojson.org/v6/assistant/ to calculate
		//	DynamicJsonDocument capacity.
		// Use extra private functions to process data if needed
		
		void setState(String jsonString) {
			char jsonCharArr[jsonString.length()];
			jsonString.toCharArray(jsonCharArr, jsonString.length());
			
			const size_t capacity = JSON_OBJECT_SIZE(5) + 120;
			DynamicJsonDocument doc(capacity);

			DeserializationError err = deserializeJson(doc, jsonCharArr);
			if(err) { 
				Serial.print(F("deserializeJson() failed with code "));
				Serial.println(err.c_str());
			}
			
			bool isOn = doc["on"];
			digitalWrite(signalPin, isOn ? HIGH : LOW);
			
			Serial.print("New state received:");
			Serial.println(isOn? "on" : "off");
		}
		
		// Use this function to send the Json document to the Raspberry Pi.
		// Names of records in the Json object must be identical to those
		//	used on the Raspberry Pi program for the device.
		// Use https://arduinojson.org/v6/assistant/ to calculate
		//	DynamicJsonDocument capacity.
		void sendSensorData(WiFiClient listenerServer) {
			// Device has no sensors
		}
};

Device thisDevice = Device();

///////////////////////////////////////////////////////////////////////////
// Listen for TCP connections
///////////////////////////////////////////////////////////////////////////

void listenForData() {
	if (server.hasClient()) {		// connecting the client to the server
		
		for (int i = 0; i < MAX_SRV_CLIENTS; i++) {
			//find free/disconnected spot
			if (!serverClients[i] || !serverClients[i].connected()) {
				if (serverClients[i]) serverClients[i].stop();
				serverClients[i] = server.available();
				continue;
			}
		}
		//no free/disconnected spot so reject
		WiFiClient serverClient = server.available();
		serverClient.stop();
	}
	
	//check clients for data
	for (int i = 0; i < MAX_SRV_CLIENTS; i++) {
		if (serverClients[i] && serverClients[i].connected()) {
			if (serverClients[i].available()) {
				String message = "";
				while (serverClients[i].available()) {
					char tempchar = serverClients[i].read();
					message += tempchar;
				}

				if (message.equals(REQUEST_FRIENDLY_ID)) {
					serverClients[i].write(verification);
				} else {
					thisDevice.setState(message);
					serverClients[i].write("MESSAGE_RECEIVED");
				}
				serverClients[i].stop();
			}
		}
	}
}

void updateData() {
	WiFiClient client;
	if (!client.connect(raspberryIp, TCP_PORT)) {
		Serial.println("TCP Connection failed, restarting UDP exchange");
		setupIpExchange();
		return;
	}

	if (client.connected()) {
		thisDevice.sendSensorData(client);
		client.println(verification);
	}
	client.stop();
}

///////////////////////////////////////////////////////////////////////////
// Setup and IP exchange
///////////////////////////////////////////////////////////////////////////

void setupWiFi() {
	Serial.print("Connecting to ");
	Serial.println(ssid);

	WiFi.hostname(verification);
	WiFi.mode(WIFI_STA);
	WiFi.begin(ssid, password);

	while (WiFi.status() != WL_CONNECTED) {
		delay(500);
		Serial.print(".");
	}
	Serial.println("");
	Serial.println("Connected to WiFi");
	Serial.print("IP address is: ");
	Serial.println(WiFi.localIP());
	IPAddress local = WiFi.localIP();

	Serial.print("Subnet mask is: ");
	Serial.println(WiFi.subnetMask());

	IPAddress mask = WiFi.subnetMask();
	broadcastIp = IPAddress(
		local[0] | (~mask[0]),
		local[1] | (~mask[1]),
		local[2] | (~mask[2]),
		local[3] | (~mask[3])
	);
	
	Serial.print("Broadcast address is: ");
	Serial.println(broadcastIp);
}

void setupIpExchange() {
	Udp.begin(UDP_PORT);
	
	bool responded = false;
	currentMs = millis();
	prevMs = currentMs;
	
	while (!responded) {
		currentMs = millis();
		broadcastLocalIp(broadcastIp);
		
		Serial.print("UDP exchange: broadcasting ID: ");
		Serial.println(verification);
		
		responded = getUdpBroadcastResponse();
	}
}

void broadcastLocalIp(IPAddress & address) {
	if (currentMs - prevMs < UDP_BROADCAST_TIMEOUT_MS) return;
	else prevMs = currentMs;

	// Reset all bytes in the buffer to 0
	memset(udpPacketBuffer, 0, UDP_PACKET_SIZE);

	char empty[] = "";
	char* packetString = strcat(strcat(empty, verification), "&end");

	sprintf((char*)udpPacketBuffer, packetString);
	if (Udp.beginPacket(address, UDP_PORT) == 0) {
		Serial.println("beginPacket failed");
		return;
	}

	Udp.write(udpPacketBuffer, UDP_PACKET_SIZE);
	if (Udp.endPacket() == 0) {
		Serial.println("endPacket failed"); 
		return;
	}
}

bool getUdpBroadcastResponse() {
	if (Udp.parsePacket()) {
		// We've received a packet, read the data from it
		raspberryIp = Udp.remoteIP();
				
		Serial.print("UDP response: RPi IP address: ");
		Serial.println(raspberryIp);
		
		memset(udpPacketBuffer, 0, UDP_PACKET_SIZE);
		Udp.read(udpPacketBuffer, UDP_PACKET_SIZE); // read the packet into the buffer
		return true;
	}
	return false;
}

void setupTcpServer() {
	server.begin();
}

void setupTimer() {
	currentMs = millis();
	prevMs = currentMs;
}
