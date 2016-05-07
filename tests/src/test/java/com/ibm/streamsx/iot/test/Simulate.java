package com.ibm.streamsx.iot.test;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.iot.spl.Schemas;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.function.UnaryOperator;
import com.ibm.streamsx.topology.spl.SPLStream;
import com.ibm.streamsx.topology.spl.SPLStreams;

public class Simulate {
    
    // Create a SPL stream that looks like device events
    public static void simulateEvents(Topology topology, int delay, boolean allowFilter, JSONObject ...payloads) {
        
        TStream<JSONObject> pys = topology.constants(Arrays.asList(payloads));
        
        SPLStream events = SPLStreams.convertStream(pys,
                (payload,tuple) -> { 
                    tuple.setString("typeId", payload.get("py_type").toString());                    
                    tuple.setString("deviceId", payload.get("py_device").toString());
                    tuple.setString("eventId", payload.get("py_event").toString());
                    
                    JSONObject ed = new JSONObject();
                    ed.put("d", payload.get("py_payload"));
                    // ISO 8601 date
                    ed.put("ts", ZonedDateTime.now().format( DateTimeFormatter.ISO_INSTANT ));
                    try {
                        tuple.setString("jsonString", ed.serialize());
                    } catch (Exception e) {
                        new RuntimeException(e);
                    }
                    return tuple;
                },
                Schemas.DEVICE_EVENT);
        
        
        events.modify(new Delay<>(delay)).publish("streamsx/iot/device/events", allowFilter);        
    }
    
    // Create a SPL stream that looks like device commands
    public static void simulateSentCommands(Topology topology, int delay, boolean allowFilter, JSONObject ...payloads) {
        
        TStream<JSONObject> pys = topology.constants(Arrays.asList(payloads));
        
        SPLStream commands = SPLStreams.convertStream(pys,
                (payload,tuple) -> { 
                    tuple.setString("typeId", payload.get("py_type").toString());                    
                    tuple.setString("deviceId", payload.get("py_device").toString());
                    tuple.setString("cmdId", payload.get("py_command").toString());
                    
                    try {
                        tuple.setString("jsonString",
                                ((JSONObject) payload.get("py_payload")).serialize());
                    } catch (Exception e) {
                        new RuntimeException(e);
                    }
                    return tuple;
                },
                Schemas.DEVICE_CMD);
        
        
        commands.modify(new Delay<>(delay)).publish("streamsx/iot/device/commands/sent", allowFilter);        
    }
    
    // Create a SPL stream that looks like device statuses
    public static void simulateStatuses(Topology topology, int delay, boolean allowFilter, JSONObject ...payloads) {
        
        TStream<JSONObject> pys = topology.constants(Arrays.asList(payloads));
        
        SPLStream commands = SPLStreams.convertStream(pys,
                (payload,tuple) -> { 
                    tuple.setString("typeId", payload.get("py_type").toString());                    
                    tuple.setString("deviceId", payload.get("py_device").toString());
                    
                    try {
                        tuple.setString("jsonString",
                                ((JSONObject) payload.get("py_payload")).serialize());
                    } catch (Exception e) {
                        new RuntimeException(e);
                    }
                    return tuple;
                },
                Schemas.DEVICE_STATUS);
        
        
        commands.modify(new Delay<>(delay)).publish("streamsx/iot/device/statuses", allowFilter);        
    }    
    /**
     * Delay to ensure that tuples are not dropped while dynamic
     * connections are being made.
     */
    @SuppressWarnings("serial")
    public static class Delay<T> implements UnaryOperator<T> {
        
        private boolean first = true;
        private long delay = 5000;
        
        public Delay(int delay) {
            this.delay = delay * 1000;
        }
        public Delay() {
        }

        @Override
        public T apply(T v)  {
            if (first) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    return null;
                }
                first = false;
            }
            
            return v;
        }
    }
}
