package com.ibm.streamsx.iot.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import java.io.File;

import org.junit.Test;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.iot.DeviceEvent;
import com.ibm.streamsx.iot.IotStreams;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.context.StreamsContext.Type;
import com.ibm.streamsx.topology.context.StreamsContextFactory;
import com.ibm.streamsx.topology.streams.StringStreams;
import com.ibm.streamsx.topology.tester.Condition;
import com.ibm.streamsx.topology.tester.Tester;
import com.ibm.streamsx.topology.spl.SPL;

public class DeviceEventsTest {
    
    @Test
    public void testDeviceEventsAllNoPubFilter() throws Exception {
        testDeviceEventsAll(false);
    }
    @Test
    public void testDeviceEventsAllPubFilter() throws Exception {
        testDeviceEventsAll(true);
    }
    
    private void testDeviceEventsAll(boolean allowFilter) throws Exception {
        
        Topology topology = new Topology();

        File iotTkLocation = new File(System.getProperty("streamsx.iot.toolkitlocation"));
        System.out.printf ("IOT ToolkitLocation used in Topology: %s", iotTkLocation);
        SPL.addToolkit(topology, iotTkLocation);
       
        JSONObject[] events = generateEvents(200);
        
        Simulate.simulateEvents(topology, 10, allowFilter, events);
        
        // Subscribe to device events and just print them to standard out.
        TStream<DeviceEvent> eventStream = IotStreams.eventsSubscribe(topology);
        
        Condition<List<String>> tuples = completeAndValidate(eventStream, 30, events);
        
        List<String> results = tuples.getResult();
        
        assertMatchingEvents(events, results);     
    }
    
    @Test
    public void testDeviceEventsSingleNoPubFilter() throws Exception {
        testDeviceEventsEventId(false, null, "E3");
    }
    @Test
    public void testDeviceEventsSinglePubFilter() throws Exception {
        testDeviceEventsEventId(true, null, "E1");
    }
    @Test
    public void testDeviceEventsMultiNoPubFilter() throws Exception {
        testDeviceEventsEventId(false, null, "E0", "E4");
    }
    @Test
    public void testDeviceEventsMultiPubFilter() throws Exception {
        testDeviceEventsEventId(true, null, "E2", "E1");
    }
    
    @Test
    public void testDeviceEventsTypeAndEventNoPubFilter1() throws Exception {
        testDeviceEventsEventId(false, new String[]{"T0", "T3"}, "E0", "E2");
    }
    @Test
    public void testDeviceEventsTypeAndEventPubFilter1() throws Exception {
        testDeviceEventsEventId(true, new String[]{"T1", "T3", "T2"}, "E2", "E1");
    }
    @Test
    public void testDeviceEventsTypeAndEventNoPubFilter2() throws Exception {
        testDeviceEventsEventId(false, new String[]{"T0"}, "E0", "E2");
    }
    @Test
    public void testDeviceEventsTypeAndEventPubFilter2() throws Exception {
        testDeviceEventsEventId(true, new String[]{"T3"}, "E2", "E1");
    }
        
    private void testDeviceEventsEventId(boolean allowFilter, String[] typeIds, String...eventIds) throws Exception {
        
        Topology topology = new Topology();
  
        File iotTkLocation = new File(System.getProperty("streamsx.iot.toolkitlocation"));
    	System.out.printf ("IOT ToolkitLocation used in Topology: %s", iotTkLocation);
    	SPL.addToolkit(topology, iotTkLocation);
        
        JSONObject[] events = generateEvents(200);
        
        Simulate.simulateEvents(topology, 10, allowFilter, events);
        
        // Subscribe to device events and just print them to standard out.
        TStream<DeviceEvent> eventStream = IotStreams.eventsSubscribe(topology, typeIds, eventIds);
        
        Set<String> ftypes = new HashSet<>();
        if (typeIds != null)
            ftypes.addAll(Arrays.asList(typeIds));
        
        Set<String> fevents = new HashSet<>();
        fevents.addAll(Arrays.asList(eventIds));
        
        // must be filtering on something
        assertFalse(ftypes.isEmpty() && fevents.isEmpty());
        
        List<JSONObject> filteredEventsL = new ArrayList<>();
        for (JSONObject event : events) {
            String tid = (String) event.get("py_type");
            String eid = (String) event.get("py_event");
            
            // just filtering on events
            if (ftypes.isEmpty()) {
                if (fevents.contains(eid))
                    filteredEventsL.add(event);
                continue;
            }
            
            // just filtering on types
            if (fevents.isEmpty()) {
                if (ftypes.contains(tid))
                    filteredEventsL.add(event);
                continue;
            }
            
            // filtering on both
            if (ftypes.contains(tid) && fevents.contains(eid))
                filteredEventsL.add(event);
        }
        events = filteredEventsL.toArray(new JSONObject[filteredEventsL.size()]);
        
        Condition<List<String>> tuples = completeAndValidate(eventStream, 30, events);
        
        List<String> results = tuples.getResult();
        
        assertMatchingEvents(events, results);     
    }
    
    private void assertMatchingEvents(JSONObject[] events, List<String> results) throws Exception {
        assertEquals(events.length, results.size());
        
        for (int i = 0 ; i < events.length; i++) {
            JSONObject event = events[i];
            JSONObject result = (JSONObject) JSON.parse(results.get(i));
            
            assertEquals(event.get("py_type"), result.get("typeId"));
            assertEquals(event.get("py_device"), result.get("deviceId"));
            assertEquals(event.get("py_event"), result.get("eventId"));
            
            assertEquals(event.get("py_data"), result.get("d"));
            
            if (event.containsKey("py_ts")) {
                assertTrue(result.containsKey("ts"));
                Instant expectedTs = Instant.ofEpochMilli((Long) event.get("py_ts"));
                assertEquals(expectedTs, Instant.parse((String) result.get("ts")));
            } else {
                assertFalse(result.containsKey("ts"));
            }
        }
    }
    
    public static JSONObject[] generateEvents(int count) {
        Random r = new Random();
        JSONObject[] events = new JSONObject[count];
        
        for (int i = 0 ; i < count ; i++) {
            JSONObject event = new JSONObject();
            
            events[i] = event;
            
            event.put("py_type", "T" + r.nextInt(4));
            event.put("py_device", "D" + r.nextInt(10));
            event.put("py_event", "E" + r.nextInt(5));
            
            if (r.nextFloat() > 0.1) {
                int offset = r.nextInt(20000) - 10000;
                event.put("py_ts", System.currentTimeMillis() + offset);
            }
            
            JSONObject payload = new JSONObject();
            event.put("py_data", payload);
            payload.put("v1", (long) r.nextInt(1000));
            payload.put("v2", "V2_" + r.nextInt(5000));
        }
        
        return events;
    }

    public static Condition<List<String>> completeAndValidate(
            TStream<?> output, int seconds, JSONObject...inputs) throws Exception {
        
        Tester tester = output.topology().getTester();
        
        TStream<String> json = StringStreams.toString(output);
        
        Condition<Long> expectedCount = tester.tupleCount(json, inputs.length);
        Condition<List<String>> tuples = tester.stringContents(json);
                
        tester.complete(
                StreamsContextFactory.getStreamsContext(Type.DISTRIBUTED_TESTER),
                new HashMap<>(),
                expectedCount,
                seconds, TimeUnit.SECONDS);

        assertTrue(expectedCount.toString(), expectedCount.valid());
        
        return tuples;
    }
}
