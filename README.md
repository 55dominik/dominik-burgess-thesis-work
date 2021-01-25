# Development of ESP8266, Raspberry Pi and Android based, remotely controllable smart home system

Thesis of Dominik Burgess

Text from install.txt:

By: Dominik Burgess

Last updated: 04/12/2020

________________________________________________________
1: Build or use pre-built Raspberry Pi app APK file

	Build the APK file of the Raspberry Pi application using android studio.
  
     OR
     
	Use the "rpi-app.apk" file found in "apk files" folder.
  

________________________________________________________
2: Create Android things image file

	Go to "https://partner.android.com/things/console"
	Log in using a google account (required)
	"Add product" - Choose "Raspberry Pi 3" from the dropdown menu
	Click on newly created model
	Click on "New" -> "Start from scratch"
	Type a name for the build configuration, click "Next"
	Choose version then click "Next" (last tested version:
				OS build: OIM1.180327.097
				OS version: 1.0.15.5796897
				Patch level: 20190801
				Date uploaded: Aug 16, 2019)
	"Add an app" -> upload the "rpi-app.apk" file and select, then click "Next"
		(ensure Google Play Services apk is added)
	Click "Next" and leave default selections
	Click "Create Build"

_______________________________________________________
3: Intall the iso file on the Raspberry Pi

	Download the build configuration you created in step 2
	Download the Android Things setup utility 
		(found: "https://partner.android.com/things/console/u/0/#/tools")
	Run the setup utility:
		"1 - Install Android THings and optionally set up Wi-Fi"
		Type 1 then [Enter]

		"1 - Raspberry Pi 3"
		Type 1 then [Enter]

		"2 - Custom image..."
		Type 2 then [Enter]

		Copy the path of the downloaded .zip file containing the image
		Plug in the SD card to install the image on

		Follow the remaining steps to set up wifi on the Raspberry Pi OR
		  use an ethernet cable instead (ensure wifi/ethernet networks are private networks).
	Plug SD card into Raspberry Pi and power on.

_______________________________________________________
4: Install the application on an android device

	Use the provided "mobile-app.apk"
     OR
	Compile using Android Studio and intall via USB cable

_______________________________________________________
5: Create and install devices

	Create devices in the mobile application
	Fill out details in the corresponding ESP8266 .ino projects
	Install the projects onto NodeMCU ESP8266 devices
	Connect appliances and sensors to NodeMCU board
	Power on NodeMCU board
