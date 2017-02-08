/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iot.spl;

import static com.ibm.streams.operator.Type.Factory.getStreamSchema;
import static com.ibm.streams.operator.Type.MetaType.RSTRING;
import static com.ibm.streamsx.topology.spl.SPLSchemas.JSON;

import com.ibm.streams.operator.StreamSchema;
import com.ibm.streamsx.topology.spl.SPLSchemas;

/**
 * SPL schemas for {@code com.ibm.streamsx.iot} toolkit.
 *
 */
public class Schemas {
    public static final String JSON_STRING = SPLSchemas.JSON.getAttribute(0).getName();

    public static final String TYPE_ID = "typeId"; //$NON-NLS-1$
    public static final String DEVICE_ID = "deviceId"; //$NON-NLS-1$
    
    public static final String EVENT_ID = "eventId"; //$NON-NLS-1$
    public static final String CMD_ID = "cmdId"; //$NON-NLS-1$


    /**
     * A device. Matches {@code com.ibm.streamsx.iot::Device}.
     */
    public static final StreamSchema DEVICE = getStreamSchema("tuple<rstring typeId, rstring deviceId>"); //$NON-NLS-1$

    /**
     * A device event. Matches {@code com.ibm.streamsx.iot::DeviceEvent}.
     */
    public static final StreamSchema DEVICE_EVENT = DEVICE.extend(RSTRING.getLanguageType(), EVENT_ID)
            .extendBySchemas(JSON);

    /**
     * A device command. Matches {@code com.ibm.streamsx.iot::DeviceCmd}.
     */
    public static final StreamSchema DEVICE_CMD = DEVICE.extend(RSTRING.getLanguageType(), CMD_ID)
            .extendBySchemas(JSON);
    
    /**
     * A device status. Matches {@code com.ibm.streamsx.iot::DeviceStatus}.
     */
    public static final StreamSchema DEVICE_STATUS = DEVICE.extendBySchemas(JSON);

}
