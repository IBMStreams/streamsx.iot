# Test

There are two test parts available:

##1. Unit test
This part covers just the mirco-service interface.

##2. End-to-End test
This part is using the IBM Watson IoT platform. It performs a real test from IoT device over IBM Watson IoT platform to an Streams topology application receiving the device data.
The IoT device is an Edgent thread. The receiving Streams application is the com.ibm.streams.iot.watson.apps::IoTPlatform application. This is an SPL application communicating with IBM WatsonIoT on the one side and the IoT toolkit mircoservice mechanism (to which other Streams application my subscribe or publish) on the other side. Another test application is using this mircoservice mechanism to receive device events and send commands.



#Running the tests

You need a running Streams domaine and instance which ID's are available in environment variables STREAMS_INSTANCE_ID and STREAMS_DOMAIN_ID.


##1. Unit test
There is no precondition to run the unit tests.
Change to 'test' directory and run "ant unittests".

##2. End-toEnd test
For this test one needs to have a Watson IoT service running on IBM Bluemix. 
For the Edgent device a one need to create a device in The Watson IoT service with DeviceType "Test" and DeviceId "Test001". 
In the 'test' directory rename the device.cfg.template to device.cfg and change "org" with your org-id and "auth-token" with the token you got for your device "Test:Test001"

Content of file device.cfg.template:

org=your_org_id
type=Test
id=Test001
auth-method=token
auth-token=your_device_auth_token

This file is used for the Edgent thread to act as your device.


The IoTPlatform application needs also credential informations to connect to your Bluemix Watson IoT service. You need to create an application key within your Bluemix Watson IoT service.
Change the file iotf.properties.template to iotf.properties and enter your data.

Content of the file iotf.properties.template

# WatsonIoT application-credentials used in hub-application
#
# enter your own application credentials here
#
iot.org=your_org_id
iot.authKey=your_authKey
iot.authToken=your_authToken


Change now on commandline to the 'test' directory and run "ant e2etests".


##3.Both tests

You can run both tests together but you need same preconditions as before.

Change on commandline to the 'test' directory and run "ant test".

