# streamsx.iotf

## Connectivity with IBM Internet of Things Foundation

Provide the ability to have an IBM Streams application easily interact with IoTF, either in Bluemix (Streaming Analytics Service) or on-premises (IBM Streams).

## Internet of Things Foundation
The IBM [https://internetofthings.ibmcloud.com/|Internet of Things Foundation] (IoTF) service lets
your IBM Streams applications communicate with and consume data collected by your
connected devices, sensors, and gateways.

oTF provides a model around devices where devices produce events (for example, sensor data)
and subscribe to commands (for example, control instructions, such as reduce maximum rpm for an engine).

Streams applications can use this toolkit to connect to IoTF to 
provide real time analytics against all the events from potentially
thousands of devices, including sending commands to specific devices based upon the analytics.
