package com.ibm.streamsx.iot;

import static com.ibm.streamsx.datetime.convert.ISO8601.fromIso8601ToMillis;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeParseException;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.Tuple;
import com.ibm.streamsx.iot.spl.Schemas;

class IotUtils {
        
    public static Device newDevice(Tuple tuple) {
        return new Device(tuple.getString(Schemas.TYPE_ID), tuple.getString(Schemas.DEVICE_ID));
    }
    
    static DeviceEvent newDeviceEvent(Tuple tuple) {
        return newDeviceEvent(
                newDevice(tuple), 
                tuple.getString(Schemas.EVENT_ID),
                tuple.getString(Schemas.JSON_STRING));
    }

    static DeviceEvent newDeviceEvent(Device device, String eventId, String serializedData) {
        JSONObject payload;
        try {
            payload = (JSONObject) JSON.parse(serializedData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        JSONObject data = (JSONObject) payload.get("d");
        
        Instant ts = null;
        if (payload.containsKey("ts")) {
            try {
                long ms = fromIso8601ToMillis((String) payload.get("ts"));
                
                ts = Instant.ofEpochMilli(ms);
            } catch (ParseException e) {
                ;
            }
        }     
        
        return new DeviceEvent(device, eventId, ts, data);
    }
    
    public static OutputTuple toTuple(DeviceCmd cmd, OutputTuple tuple) {
        
        tuple.setString(Schemas.TYPE_ID, cmd.getDevice().getTypeId());
        tuple.setString(Schemas.DEVICE_ID, cmd.getDevice().getId());
        tuple.setString(Schemas.CMD_ID, cmd.getCmdId());
        
        
        JSONObject payload = new JSONObject();
        payload.put("d", cmd.getData());
        
        Instant ts = cmd.getTs();
        
        if (ts == null)
            ts = Instant.now();        
        payload.put("ts", ts.toString());
        
        try {
            tuple.setString(Schemas.JSON_STRING, payload.serialize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tuple;
    }
    
    public static DeviceCmd newDeviceCmd(Tuple tuple) {
        return newDeviceCmd(newDevice(tuple),
                tuple.getString(Schemas.CMD_ID),
                tuple.getString(Schemas.JSON_STRING));
    }

    static DeviceCmd newDeviceCmd(Device device, String cmdId, String serializedData) {
        JSONObject payload;
        try {
            payload = (JSONObject) JSON.parse(serializedData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        JSONObject data = (JSONObject) payload.get("d");
        
        Instant ts = null;
        if (payload.containsKey("ts")) {
            try {
                ts = Instant.parse((String) payload.get("ts"));
            } catch (DateTimeParseException e) {
                ;
            }
        }     
        
        return new DeviceCmd(device, cmdId, ts, data);
    }
}
