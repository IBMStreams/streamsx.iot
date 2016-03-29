package com.ibm.streamsx.iotf;

import java.io.IOException;
import java.io.Serializable;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;

public class DeviceEvent implements Serializable {
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
    public static DeviceEvent newDeviceEvent(String deviceType, String deviceId, String eventId, String serializedData) {      
        JSONObject payload;
        try {
            payload = (JSONObject) JSON.parse(serializedData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }        
        return new DeviceEvent(new Device(deviceType, deviceId), eventId, payload);       
    }
    public static DeviceEvent newDeviceEvent(Device device, String eventId, String serializedPayload) throws IOException {      
        JSONObject payload = (JSONObject) JSON.parse(serializedPayload);        
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
    
    public JSONObject toJson() {
        JSONObject json = getDevice().toJson();
        json.put("eventId", getEventId());
        json.put("ts", getTs());
        json.put("payload", getPayload());
        return json;
    }
    @Override
    public String toString() {
        try {
            return toJson().serialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    
}
