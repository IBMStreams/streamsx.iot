/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iotf;

import java.io.IOException;
import java.io.Serializable;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;
import com.ibm.streams.operator.Tuple;
import com.ibm.streamsx.topology.tuple.JSONAble;

/**
 * A device event.
 */
public class DeviceEvent implements JSONAble, Serializable {
    public static final String EVENT_ID = "eventId";
    
    private static final long serialVersionUID = 1L;
    
    private final Device device;
    private final String eventId;
    private final String ts;
    private final JSONObject payload;
    
    public DeviceEvent(Device device, String eventId, JSONObject data) {
        super();
        this.device = device;
        this.eventId = eventId;
        this.ts = (String) data.get("ts");
        this.payload = (JSONObject) data.get("d");
        
    }
    public static DeviceEvent newDeviceEvent(Tuple tuple) {
        return newDeviceEvent(
                Device.newDevice(tuple),
                tuple.getString(EVENT_ID),
                tuple.getString(Device.JSON));
    }
    public static DeviceEvent newDeviceEvent(Device device, String eventId, String serializedData) {      
        JSONObject payload;
        try {
            payload = (JSONObject) JSON.parse(serializedData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }        
        return new DeviceEvent(device, eventId, payload);       
    }

    public Device getDevice() {
        return device;
    }

    public String getEventId() {
        return eventId;
    }

    public JSONObject getPayload() {
        return payload;
    }
    public String getTs() {
        return ts;
    }
    
    @Override
    public JSONObject toJSON() {
        JSONObject json = getDevice().toJSON();
        json.put(EVENT_ID, getEventId());
        json.put("ts", getTs());
        json.put("payload", getPayload());
        return json;
    }
    @Override
    public String toString() {
        try {
            return toJSON().serialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    
}
