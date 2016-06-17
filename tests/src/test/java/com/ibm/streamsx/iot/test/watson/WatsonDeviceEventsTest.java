package com.ibm.streamsx.iot.test.watson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.iot.DeviceEvent;
import com.ibm.streamsx.iot.IotStreams;
import com.ibm.streamsx.iot.test.DeviceEventsTest;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.tester.Condition;

public class WatsonDeviceEventsTest {
       
    @Test
    public void testDeviceEventsAll() throws Exception {
        
        Topology topology = new Topology();
        
        // Generate events, but this test will ignore the device id and type
        // since it is fixed by the deviceCfg.
        JSONObject[] events = DeviceEventsTest.generateEvents(20);
                
        // Subscribe to device events and just print them to standard out.
        TStream<DeviceEvent> eventStream = IotStreams.eventsSubscribe(topology);
        eventStream = eventStream.filter(e -> !e.getEventId().equals("heartbeat"));
        Edgent2Watson.go(topology);
             
        Edgent2Watson.createEvents(events);

        Condition<List<String>> tuples = DeviceEventsTest.completeAndValidate(eventStream, 3000, events);
        assertFalse(Edgent2Watson.FAILED.get());
        
        List<String> results = tuples.getResult();
        
        assertMatchingEvents(events, results);     
    }
    
    private void assertMatchingEvents(JSONObject[] events, List<String> results) throws Exception {
        assertEquals(events.length, results.size());
        
        for (int i = 0 ; i < events.length; i++) {
            JSONObject event = events[i];
            JSONObject result = (JSONObject) JSON.parse(results.get(i));
            
            //assertEquals(event.get("py_type"), result.get("typeId"));
            //assertEquals(event.get("py_device"), result.get("deviceId"));
            assertEquals(event.get("py_event"), result.get("eventId"));
            
            assertEquals(event.get("py_data"), result.get("d"));
            
            System.err.println("RESULT:" + result.toString());
            
            assertTrue(result.containsKey("ts"));
        }
    }
}
