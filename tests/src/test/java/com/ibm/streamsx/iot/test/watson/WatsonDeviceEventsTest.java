package com.ibm.streamsx.iot.test.watson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import java.io.File;

import org.junit.Test;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.iot.DeviceEvent;
import com.ibm.streamsx.iot.IotStreams;
import com.ibm.streamsx.iot.test.DeviceEventsTest;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.streams.StringStreams;
import com.ibm.streamsx.topology.tester.Condition;
import com.ibm.streamsx.topology.tester.Tester;
import com.ibm.streamsx.topology.spl.SPL;

public class WatsonDeviceEventsTest {
       
    @Test
    public void testDeviceEventsAll() throws Exception {
        
        File iotTkLocation = new File(System.getProperty("streamsx.iot.toolkitlocation"));
        
        Topology topology = new Topology();

        System.out.printf ("IOT ToolkitLocation used in Topology: %s", iotTkLocation);
        SPL.addToolkit(topology,iotTkLocation);
       
        // Generate events, but this test will ignore the device id and type
        // since it is fixed by the deviceCfg.
        JSONObject[] events = DeviceEventsTest.generateEvents(20);
                
        // Subscribe to device events and just print them to standard out.
        TStream<DeviceEvent> eventStream = IotStreams.eventsSubscribe(topology);
        Edgent2Watson.go(topology);
        
        String filterEventId = events[7].get("py_event").toString();
        List<JSONObject> filteredEvents = new ArrayList<>();
        for (JSONObject event : events) {
            if (filterEventId.equals(event.get("py_event"))) {
                filteredEvents.add(event);
            }      
        }
        assertFalse(filteredEvents.isEmpty());
        JSONObject[] fea = filteredEvents.toArray(new JSONObject[filteredEvents.size()]);
        
        // Subscribe to device events and just print them to standard out.
        TStream<DeviceEvent> eventStreamFiltered = IotStreams.eventsSubscribe(topology, filterEventId);
        Tester tester = topology.getTester();
        
        TStream<String> json = StringStreams.toString(eventStreamFiltered);       
        Condition<List<String>> tuplesFiltered = tester.stringContents(json);

            
        Edgent2Watson.createEvents(events);

        Condition<List<String>> tuples = DeviceEventsTest.completeAndValidate(eventStream, 60, events);
        Edgent2Watson.stop();
        assertFalse(Edgent2Watson.FAILED.get());
        
        List<String> results = tuples.getResult();
        
        assertMatchingEvents(events, results);  
        
        assertMatchingEvents(fea, tuplesFiltered.getResult());
    }
    
    private void assertMatchingEvents(JSONObject[] events, List<String> results) throws Exception {
        assertEquals(events.length, results.size());
        
        for (int i = 0 ; i < events.length; i++) {
            JSONObject event = events[i];
            JSONObject result = (JSONObject) JSON.parse(results.get(i));
            
            assertEquals(event.get("py_event"), result.get("eventId"));
            
            assertEquals(event.get("py_data"), result.get("d"));
            
            assertTrue(result.containsKey("ts"));
        }
    }
}
