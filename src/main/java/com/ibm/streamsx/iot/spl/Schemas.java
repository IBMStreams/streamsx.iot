/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iot.spl;

import static com.ibm.streams.operator.Type.Factory.getStreamSchema;
import static com.ibm.streams.operator.Type.MetaType.RSTRING;
import static com.ibm.streamsx.iot.DeviceCmd.CMD_ID;
import static com.ibm.streamsx.iot.DeviceEvent.EVENT_ID;
import static com.ibm.streamsx.topology.spl.SPLSchemas.JSON;

import com.ibm.streams.operator.StreamSchema;

/**
 * SPL schemas for {@code com.ibm.streamsx.iot} toolkit.
 *
 */
public class Schemas {

    /**
     * A device. Matches {@code com.ibm.streamsx.iot::Device}.
     */
    public static final StreamSchema DEVICE = getStreamSchema("tuple<rstring typeId, rstring deviceId>");

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
