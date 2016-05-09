/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iot;

import com.ibm.streamsx.iot.spl.IotSPLStreams;
import com.ibm.streamsx.iot.spl.Schemas;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.TopologyElement;
import com.ibm.streamsx.topology.spl.SPLStream;
import com.ibm.streamsx.topology.spl.SPLStreams;

/**
 * IBM Watson IoT Platform streams.
 * 
 * These streams using the micro-service model promoted by the
 * {@code com.ibm.streamsx.iot} toolkit. In order to interact with IoT Platform
 * the Streams application
 * {@code com.ibm.streamsx.iot.watson.apps::IotPlatform} must be running.
 *
 */
public class IotStreams {

    private IotStreams() {
    }

    /**
     * Subscribe to events as {@code DeviceEvent} instances.
     * 
     * Subscribes to all device events with event identifiers listed in
     * {@code eventId}. If {@code eventId} is empty or passed as a {@code null}
     * array then all device events are subscribed to.
     * 
     * To receive device events the Streams application
     * {@code com.ibm.streamsx.iot.watson.apps::IotPlatform} must be
     * running in the same Streams instance.
     * 
     * @param te
     *            Topology to create this source in.
     * @param eventId
     *            Event identifiers to subscribe to. If none are provided then
     *            all event identifiers are subscribed to.
     * @return Stream containing device events.
     * 
     */
    public static TStream<DeviceEvent> eventsSubscribe(TopologyElement te, String... eventId) {

        return eventsSubscribe(te, null, eventId);
    }
    
    /**
     * Subscribe to events as {@code DeviceEvent} instances.
     * 
     * Subscribes to all device events with:
     * <UL>
     * <LI>All device type identifiers listed in {@code typeIds}. If
     * {@code typeIds} is empty or passed as a {@code null} array reference
     * then no filtering against type identifier is applied.</LI>
     * <LI>AND</LI>
     * <LI>All device event identifiers listed in {@code eventId}. If
     * {@code eventId} is empty or passed as a {@code null} array reference
     * then no filtering against event identifier is applied.</LI>
     * </UL>
     * 
     * 
     * To receive device events the Streams application
     * {@code com.ibm.streamsx.iot.watson.apps::IotPlatform} must be
     * running in the same Streams instance.
     * 
     * @param te
     *            Topology to create this source in.
     * @param typeIds Type identifers to subscribe to. If this is empty or null then
     * all type identifiers are subscribed to.
     * @param eventId
     *            Event identifiers to subscribe to. If none are provided then
     *            all event identifiers are subscribed to.
     * @return Stream containing device events.
     * 
     */
    public static TStream<DeviceEvent> eventsSubscribe(TopologyElement te, String[] typeIds, String... eventId) {

        SPLStream splEvents = IotSPLStreams.eventsSubscribe(te, typeIds, eventId);
        TStream<DeviceEvent> events = splEvents.transform(IotUtils::newDeviceEvent);
        return events;
    }    
    

    /**
     * Subscribe to commands as {@code DeviceCmd} instances.
     * 
     * Subscribes to all device commands with commands identifiers listed in
     * {@code cmdId}. If {@code cmdId} is empty or passed as a {@code null}
     * array then all device commands are subscribed to.
     * 
     * To receive device events the Streams application
     * {@code com.ibm.streamsx.iot.watson.apps::IotPlatform} must be
     * running in the same Streams instance.
     * 
     * @param te
     *            Topology to create this source in.
     * @param cmdId
     *            Command identifiers to subscribe to.
     * @return Stream containing device events.
     * 
     */
    public static TStream<DeviceCmd> commandsSubscribe(TopologyElement te, String... cmdId) {
        return commandsSubscribe(te, null, cmdId);
    }
    
    public static TStream<DeviceCmd> commandsSubscribe(TopologyElement te, String[] typeIds, String... cmdId) {

        SPLStream splCommands = IotSPLStreams.commandsSubscribe(te, typeIds, cmdId);
        TStream<DeviceCmd> commands = splCommands.transform(IotUtils::newDeviceCmd);
        return commands;
    }

    public static void commandPublish(TStream<DeviceCmd> commandStream) {
        SPLStream splCommands = SPLStreams.convertStream(commandStream, IotUtils::toTuple,
                Schemas.DEVICE_CMD);

        IotSPLStreams.commandPublish(splCommands);
    }

    public static TStream<DeviceStatus> statusesSubscribe(TopologyElement te, String ... typeId) {

        SPLStream splStatuses = IotSPLStreams.statusesSubscribe(te, typeId);
        TStream<DeviceStatus> statuses = splStatuses.transform(DeviceStatus::newDeviceStatus);
        return statuses;
    }
}
