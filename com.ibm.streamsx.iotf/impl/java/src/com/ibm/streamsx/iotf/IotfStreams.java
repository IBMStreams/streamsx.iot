/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iotf;

import com.ibm.streamsx.iotf.spl.IotfSPLStreams;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.TopologyElement;
import com.ibm.streamsx.topology.spl.SPLStream;

/**
 * IBM Watson IoT Platform streams.
 * 
 * These streams using the micro-service model promoted
 * by the {@code com.ibm.streamsx.iotf} toolkit. In order to
 * interact with IoT Platform the SPL application
 * {@code com.ibm.streamsx.iotf.apps::IotfOrganization}
 * must be running.
 *
 */
public class IotfStreams {
    
    private IotfStreams() {}
    
    /**
     * Subscribe to events as {@code DeviceEvent} instances.
     * 
     * Subscribes to all device events with event identifiers
     * listed in {@code eventId}. If {@code eventId} is empty
     * or passed as a {@code null} array then all device events
     * are subscribed to.
     * 
     * To receive device events the SPL application
     * {@code com.ibm.streamsx.iotf.apps::IotfOrganization}
     * must be running in the same instance.
     * 
     * @param te Topology to create this source in.
     * @param eventId Event identifiers to subscribe to.
     * @return Stream containing device events.
     * 
     */
    public static TStream<DeviceEvent> eventsSubscribe(TopologyElement te, String ...eventId) {
        
        SPLStream splEvents = IotfSPLStreams.eventsSubscribe(te, eventId);      
        TStream<DeviceEvent> events = splEvents.transform(e -> DeviceEvent.newDeviceEvent(
                    e.getString("typeId"),
                    e.getString("deviceId"), 
                    e.getString("eventId"),
                    e.getString("jsonString")));
        
        return events;
    }

}
