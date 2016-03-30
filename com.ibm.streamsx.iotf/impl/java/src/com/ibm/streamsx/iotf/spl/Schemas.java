/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iotf.spl;

import static com.ibm.streams.operator.Type.Factory.getStreamSchema;

import com.ibm.streams.operator.StreamSchema;

/**
 * SPL schemas for {@code com.ibm.streamsx.iotf} toolkit.
 *
 */
public class Schemas {
    
    /**
     * A device.
     * Matches {@code com.ibm.streamsx.iotf::Device}. 
     */
    public static final StreamSchema DEVICE = 
            getStreamSchema("tuple<rstring typeId, rstring deviceId>");
    
    /**
     * A device event.
     * Matches {@code com.ibm.streamsx.iotf::DeviceEvent}. 
     */
    public static final StreamSchema DEVICE_EVENT = 
            DEVICE.extendBySchemas(getStreamSchema("tuple<rstring eventId, rstring jsonString>"));

}
