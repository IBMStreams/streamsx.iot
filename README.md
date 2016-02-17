# streamsx.iotf

[SPLDOC for com.ibm.streamsx.iotf toolkit](http://ibmstreams.github.io/streamsx.iotf/doc/spldoc/html/index.html)

## Connectivity with IBM Watson IoT Platform

Provide the ability to have an IBM Streams application easily interact with IBM Watson IoT Platform, either in Bluemix (Streaming Analytics Service) or on-premises (IBM Streams).

## IBM Watson IoT Platform
The IBM [Watson IoT Platform](https://internetofthings.ibmcloud.com/) service lets
your IBM Streams applications communicate with and consume data collected by your
connected devices, sensors, and gateways.

IBM Watson IoT Platform provides a model around devices where devices produce events (for example, sensor data)
and subscribe to commands (for example, control instructions, such as reduce maximum rpm for an engine).

Streams applications can use this toolkit to 
provide real time analytics against all the events from potentially
thousands of devices, including sending commands to specific devices based upon the analytics.

## Quarks

[Quarks](http://quarks-edge.github.io/) is a programming model and runtime that can be embedded in gateways and devices. An open source solution for implementing and deploying edge analytics on varied data streams and devices.

Quarks interacts with IBM Streams through IBM Watson IoT Plaform.  Quarks locally separates the interesting from the mundane, so you don’t have to send every sensor reading over a network. If 99% of readings are normal, Quarks detects the 1% anomalies and just sends those as device events for further analysis with IBM Streams.

IBM Streams applications analyze device events from Quarks applications and then can control individual devices by sending device commands based upon the analytics.
