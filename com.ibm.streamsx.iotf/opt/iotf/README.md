MQTT SSL connections are described here:
* https://docs.internetofthings.ibmcloud.com/applications/mqtt.html

`messaging.pem` was taken from this URL at this commit id: 2d874cc4b5b7b13630e3d049d849496820c6c6f8 
* https://github.com/ibm-messaging/iot-python/blob/master/src/ibmiotf/messaging.pem

`messaging.pem` contains the entire certificate chain for *.messaging.internetofthings.ibmcloud.com

The MQTT operators require a trust store, so `messaging.pem` was split into three files `m1.pem`, `m2.pem`, `m3.pem` each containing a single certificate for import into the trust store using:

* `keytool -import -file m1.pem -alias messaging1 -keystore messaging.ts -storepass streamsx.iotf.123`
* `keytool -import -file m2.pem -alias messaging2 -keystore messaging.ts -storepass streamsx.iotf.123`
* `keytool -import -file m3.pem -alias messaging3 -keystore messaging.ts -storepass streamsx.iotf.123`

The password for the trust store is `streamsx.iotf.123`, note that the trust store only contains public certificates.
