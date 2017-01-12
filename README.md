# com.ibm.streamsx.iot

## Overview

A toolkit that provides Internet of Things (IoT) functionality for IBM Streams,
including:

 * IoT device model
 * Separation of device connectivty and analytics.
 * Application integration with IBM Watson IoT Platform
 * Application integration with Apache Edgent

## IoT device model.

A generic IoT device model where:
 * Devices have a type (e.g. Raspberry_Pi_B) and a identifier, with the pair being unique.
 * Devices send events comprising of a event identifier and an event specific payload.
 * Devices receive commands comprising of a command identifier and a command specific payload.

Devices typically communicate through an internet of things scale message hub to send *device events* and receive *device commands*.

IBM Streams applications subscribe to device events and device commands to perform analytics against the state of device and other revelant data (such as weather, systems of record etc.). The result of these analytics may result in *device commands* being sent to the device to alter its behaviour etc.

The device model matches the ones used by Apache Edgent and IBM Watson IoT Platform for devices connected to Streams through a message hub.

## Microservice architecture

This toolkits uses the streaming publish-subscribe model within IBM Streams applications to separate connectivity to the message hub from analytical applications. An independent applications connects to the message hub and publishes streamsfor device events, device commands and device statues (if supported by the message hub). Analytics applications then subscribe to device events and or commands of interest.

In addition the message hub connectivity application subscribes to streams published by applications wanting to send device commands.

## Integration with IBM Watson IoT Platform

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

## Apache Edgent

[Edgent](http://edgent.apache.org/) is a programming model and runtime that can be embedded in gateways and devices. An open source solution for implementing and deploying edge analytics on varied data streams and devices.

Edgent interacts with IBM Streams through IBM Watson IoT Plaform.  Edgent locally separates the interesting from the mundane, so you don’t have to send every sensor reading over a network. If 99% of readings are normal, Edgent detects the 1% anomalies and just sends those as device events for further analysis with IBM Streams.

IBM Streams applications analyze device events from Edgent applications and then can control individual devices by sending device commands based upon the analytics.
