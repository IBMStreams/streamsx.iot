/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iot;

import java.io.IOException;
import java.io.Serializable;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;
import com.ibm.streams.operator.Tuple;
import com.ibm.streamsx.topology.tuple.JSONAble;

/**
 * A device status.
 */
public class DeviceStatus implements JSONAble, Serializable {

    private static final long serialVersionUID = 1L;

    private final Device device;
    private final JSONObject payload;

    public DeviceStatus(Device device, JSONObject payload) {
        super();
        this.device = device;
        this.payload = payload;

    }

    public static DeviceStatus newDeviceStatus(Tuple tuple) {
        return newDeviceStatus(Device.newDevice(tuple), tuple.getString(Device.JSON));
    }

    public static DeviceStatus newDeviceStatus(Device device,String serializedPayload) {
        JSONObject payload;
        try {
            payload = (JSONObject) JSON.parse(serializedPayload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new DeviceStatus(device, payload);
    }

    public Device getDevice() {
        return device;
    }

    public JSONObject getPayload() {
        return payload;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = getDevice().toJSON();
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
