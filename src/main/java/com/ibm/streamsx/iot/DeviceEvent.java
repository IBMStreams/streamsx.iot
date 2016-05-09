/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iot;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;

import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.iot.spl.Schemas;
import com.ibm.streamsx.topology.tuple.JSONAble;

/**
 * A device event.
 */
public class DeviceEvent implements JSONAble, Serializable {

    private static final long serialVersionUID = 1L;

    private final Device device;
    private final String eventId;
    private final Instant ts;
    private final JSONObject data;
    
    public DeviceEvent(Device device, String eventId, Instant ts, JSONObject data) {
        super();
        this.device = device;
        this.eventId = eventId;
        this.ts = ts;
        this.data = data;
    }

    public Device getDevice() {
        return device;
    }

    public String getEventId() {
        return eventId;
    }

    public JSONObject getData() {
        return data;
    }

    public Instant getTs() {
        return ts;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = getDevice().toJSON();
        json.put(Schemas.EVENT_ID, getEventId());
        if (getTs() != null)
            json.put("ts", getTs().toString());
        json.put("d", getData());
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
