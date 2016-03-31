/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iotf;

import com.ibm.streamsx.iotf.spl.IotfSPLStreams;
import com.ibm.streamsx.iotf.spl.Schemas;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.TopologyElement;
import com.ibm.streamsx.topology.spl.SPL;
import com.ibm.streamsx.topology.spl.SPLStream;
import com.ibm.streamsx.topology.spl.SPLStreams;

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
     * must be running in the same Streams instance.
     * 
     * @param te Topology to create this source in.
     * @param eventId Event identifiers to subscribe to.
     * @return Stream containing device events.
     * 
     */
    public static TStream<DeviceEvent> eventsSubscribe(TopologyElement te, String ...eventId) {
        
        SPLStream splEvents = IotfSPLStreams.eventsSubscribe(te, eventId);      
        TStream<DeviceEvent> events = splEvents.transform(DeviceEvent::newDeviceEvent);        
        return events;
    }
    
    /**
     * Subscribe to commands as {@code DeviceCmd} instances.
     * 
     * Subscribes to all device commands with commands identifiers
     * listed in {@code cmdId}. If {@code cmdId} is empty
     * or passed as a {@code null} array then all device commands
     * are subscribed to.
     * 
     * To receive device events the SPL application
     * {@code com.ibm.streamsx.iotf.apps::IotfOrganization}
     * must be running in the same Streams instance.
     * 
     * @param te Topology to create this source in.
     * @param cmdId Command identifiers to subscribe to.
     * @return Stream containing device events.
     * 
     */
    public static TStream<DeviceCmd> commandsSubscribe(TopologyElement te, String ...cmdId) {
        
        SPLStream splCommands = IotfSPLStreams.commandsSubscribe(te, cmdId);      
        TStream<DeviceCmd> commands = splCommands.transform(DeviceCmd::newDeviceCmd);        
        return commands;
    }
    
    public static void commandPublish(TStream<DeviceCmd> commandStream) {
        SPLStream splCommands = SPLStreams.convertStream(commandStream,
                (cmd,tuple) -> cmd.toTuple(tuple),
                Schemas.DEVICE_CMD);
        
        IotfSPLStreams.commandPublish(splCommands);
    }

}
