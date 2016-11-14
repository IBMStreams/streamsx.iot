/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iot.spl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.streams.operator.Attribute;
import com.ibm.streams.operator.Type;
import com.ibm.streams.operator.logging.LogLevel;
import com.ibm.streamsx.iot.i18n.Messages;
import com.ibm.streamsx.topology.TopologyElement;
import com.ibm.streamsx.topology.spl.SPL;
import com.ibm.streamsx.topology.spl.SPLStream;

/**
 * IBM Watson IoT Platform SPL streams.
 * 
 * These streams using the microservice model promoted by the
 * {@code com.ibm.streamsx.iot} toolkit. In order to interact with IoT Platform
 * the SPL application
 * {@code com.ibm.streamsx.iot.watson.apps::IotfOrganization} must be running.
 *
 */
public class IotSPLStreams {
	
	private static Logger l = Logger.getLogger(IotSPLStreams.class.getCanonicalName());

    private IotSPLStreams() {
    }

    /**
     * Subscribe to device events as SPL tuples.
     * 
     * Subscribes to all device events with event identifiers listed in
     * {@code eventId}. If {@code eventId} is empty or passed as a {@code null}
     * array then all device events are subscribed to.
     * 
     * To receive device events the SPL application
     * {@code com.ibm.streamsx.iot.watson.apps::IotfOrganization} must be
     * running in the same Streams instance.
     * 
     * @param te
     *            Topology to create this source in.
     * @param eventId
     *            Event identifiers to subscribe to. If no event identf
     * @return Stream containing device events.
     * 
     * @see com.ibm.streamsx.iot.IotStreams#eventsSubscribe(TopologyElement,
     *      String...)
     */   
    public static SPLStream eventsSubscribe(TopologyElement te, String[] typeIds, String... eventId) {

        Map<String, Object> params = new HashMap<>();
        if (typeIds != null && typeIds.length != 0)
            params.put("typeIds", listRStringParam(typeIds)); //$NON-NLS-1$

        if (eventId != null && eventId.length != 0)
            params.put("eventIds", listRStringParam(eventId));     //$NON-NLS-1$

        return SPL.invokeSource(te, "com.ibm.streamsx.iot::EventsSubscribe", params, Schemas.DEVICE_EVENT); //$NON-NLS-1$
    }
    
    public static SPLStream statusesSubscribe(TopologyElement te, String ... typeId) {

        Map<String, Object> params = new HashMap<>();
        if (typeId != null && typeId.length != 0)
            params.put("typeIds", listRStringParam(typeId)); //$NON-NLS-1$
 
        return SPL.invokeSource(te, "com.ibm.streamsx.iot::StatusesSubscribe", params, Schemas.DEVICE_STATUS); //$NON-NLS-1$
    }

    /**
     * Subscribe to sent device commands as SPL tuples.
     * 
     * Subscribes to all device commands with command identifiers listed in
     * {@code cmdId}. If {@code cmdId} is empty or passed as a {@code null}
     * array then all device commands are subscribed to.
     * 
     * To receive device events the SPL application
     * {@code com.ibm.streamsx.iot.watson.apps::IotfOrganization} must be
     * running in the same Streams instance.
     * 
     * @param te
     *            Topology to create this source in.
     * @param cmdId
     *            Command identifiers to subscribe to. If no command identifier
     *            is passed then all commands are subscribed to.
     * @return Stream containing device events.
     * 
     * @see com.ibm.streamsx.iot.IotStreams#eventsSubscribe(TopologyElement,
     *      String...)
     */
    public static SPLStream commandsSubscribe(TopologyElement te, String[] typeIds, String... cmdId) {

        Map<String, Object> params = new HashMap<>();
        if (typeIds != null && typeIds.length != 0)
            params.put("typeIds", listRStringParam(typeIds)); //$NON-NLS-1$

        if (cmdId != null && cmdId.length != 0)
            params.put("cmdIds", listRStringParam(cmdId)); //$NON-NLS-1$

        return SPL.invokeSource(te, "com.ibm.streamsx.iot::CommandsSubscribe", params, Schemas.DEVICE_CMD); //$NON-NLS-1$
    }

    private static Object listRStringParam(String[] values) {
        List<String> literals = new ArrayList<>(values.length);
        for (String v : values)
            literals.add("\"" + v + "\""); //$NON-NLS-1$ //$NON-NLS-2$

        return new Attribute() {

            @Override
            public int getIndex() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public String getName() {
                return literals.toString();
            }

            @Override
            public Type getType() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean same(Attribute arg0) {
                return false;
            }

        };
    }

    public static void commandPublish(SPLStream commandStream) {
        if (!commandStream.getSchema().equals(Schemas.DEVICE_CMD))
        {
        	l.log(LogLevel.ERROR, Messages.getString("SCHEMA_IS_INVALID"), new Object[]{commandStream.getSchema().getLanguageType()}); //$NON-NLS-1$
            throw new IllegalArgumentException("Schema is invalid: " + commandStream.getSchema().getLanguageType()); //$NON-NLS-1$
        }
        SPL.invokeSink("com.ibm.streamsx.iot::CommandPublish", commandStream, null); //$NON-NLS-1$
    }
}
