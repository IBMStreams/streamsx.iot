/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iot;

import java.io.IOException;
import java.io.Serializable;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.Tuple;
import com.ibm.streamsx.topology.tuple.JSONAble;

/**
 * A device command.
 */
public class DeviceCmd implements JSONAble, Serializable {
    private static final long serialVersionUID = 1L;
    
    public static final String CMD_ID = "cmdId";
    
    private final Device device;
    private final String cmdId;
    private final JSONObject payload;
    
    public DeviceCmd(Device device, String cmdId, JSONObject payload) {
        super();
        this.device = device;
        this.cmdId = cmdId;
        this.payload = payload;      
    }
    
    public static DeviceCmd newDeviceCmd(Tuple tuple) {
        return newDeviceCmd(
                Device.newDevice(tuple),
                tuple.getString(CMD_ID),
                tuple.getString(Device.JSON));
    }
    public static DeviceCmd newDeviceCmd(Device device, String cmdId, String serializedData) {      
        JSONObject payload;
        try {
            payload = (JSONObject) JSON.parse(serializedData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }        
        return new DeviceCmd(device, cmdId, payload);       
    }
   
    
    public OutputTuple toTuple(OutputTuple tuple) {
        tuple.setString(Device.TYPE_ID, getDevice().getTypeId());
        tuple.setString(Device.DEVICE_ID, getDevice().getId());
        tuple.setString(DeviceCmd.CMD_ID, getCmdId());
        try {
            tuple.setString(Device.JSON, getPayload().serialize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tuple;
    }

    public Device getDevice() {
        return device;
    }

    public String getCmdId() {
        return cmdId;
    }

    public JSONObject getPayload() {
        return payload;
    }
    
    @Override
    public JSONObject toJSON() {
        JSONObject json = getDevice().toJSON();
        json.put("cmdId", getCmdId());
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
