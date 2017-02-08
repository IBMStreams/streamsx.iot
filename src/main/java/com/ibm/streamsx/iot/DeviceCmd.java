/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iot;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;

import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.topology.tuple.JSONAble;

/**
 * A device command.
 */
public class DeviceCmd implements JSONAble, Serializable {
    private static final long serialVersionUID = 1L;

    private final Device device;
    private final String cmdId;
    private final Instant ts;
    private final JSONObject data;
    
    public DeviceCmd(Device device, String cmdId, Instant ts, JSONObject data) {
        super();
        this.device = device;
        this.cmdId = cmdId;
        this.ts = ts;
        this.data = data;
    }

    public Device getDevice() {
        return device;
    }

    public String getCmdId() {
        return cmdId;
    }
    
    public Instant getTs() {
        return ts;
    }

    public JSONObject getData() {
        return data;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = getDevice().toJSON();
        json.put("cmdId", getCmdId()); //$NON-NLS-1$
        json.put("d", getData()); //$NON-NLS-1$
        if (getTs() != null)
            json.put("ts", getTs().toString()); //$NON-NLS-1$
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
