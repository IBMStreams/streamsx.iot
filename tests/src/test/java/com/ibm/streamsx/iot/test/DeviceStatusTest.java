package com.ibm.streamsx.iot.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.iot.DeviceStatus;
import com.ibm.streamsx.iot.IotStreams;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.context.StreamsContext.Type;
import com.ibm.streamsx.topology.context.StreamsContextFactory;
import com.ibm.streamsx.topology.streams.StringStreams;
import com.ibm.streamsx.topology.tester.Condition;
import com.ibm.streamsx.topology.tester.Tester;

public class DeviceStatusTest {
    
    @Test
    public void testDeviceStatussAllNoPubFilter() throws Exception {
        testDeviceStatussAll(false);
    }
    @Test
    public void testDeviceStatussAllPubFilter() throws Exception {
        testDeviceStatussAll(true);
    }
    
    private void testDeviceStatussAll(boolean allowFilter) throws Exception {
        
        Topology topology = new Topology();
        
        JSONObject[] statuses = generateStatuses(200);
        
        Simulate.simulateStatuses(topology, 5, allowFilter, statuses);
        
        TStream<DeviceStatus> statusStream = IotStreams.statusesSubscribe(topology);
        
        Condition<List<String>> tuples = completeAndValidate(statusStream, 30, statuses);
        
        List<String> results = tuples.getResult();
        
        assertMatchingStatuses(statuses, results);     
    }
    
    @Test
    public void testDeviceStatussSingleNoPubFilter() throws Exception {
        testDeviceStatussTypeId(false, null, "T3");
    }
    @Test
    public void testDeviceStatussSinglePubFilter() throws Exception {
        testDeviceStatussTypeId(true, null, "T1");
    }
    @Test
    public void testDeviceStatussMultiNoPubFilter() throws Exception {
        testDeviceStatussTypeId(false, null, "T0", "T4");
    }
    @Test
    public void testDeviceStatussMultiPubFilter() throws Exception {
        testDeviceStatussTypeId(true, null, "T2", "T1");
    }
        
    private void testDeviceStatussTypeId(boolean allowFilter, String ... typeId) throws Exception {
        
        Topology topology = new Topology();
        
        JSONObject[] statuses = generateStatuses(200);
        
        Simulate.simulateStatuses(topology, 5, allowFilter, statuses);
        
        TStream<DeviceStatus> statusStream = IotStreams.statusesSubscribe(topology, typeId);
                
        Set<String> ftypes = new HashSet<>();
        ftypes.addAll(Arrays.asList(typeId));
                
        List<JSONObject> filteredStatusesL = new ArrayList<>();
        for (JSONObject status : statuses) {
            String tid = (String) status.get("py_type");
                        
            // just filtering on types
            if (ftypes.contains(tid))
                filteredStatusesL.add(status);
        }
        statuses = filteredStatusesL.toArray(new JSONObject[filteredStatusesL.size()]);
        
        Condition<List<String>> tuples = completeAndValidate(statusStream, 30, statuses);
        
        List<String> results = tuples.getResult();
        
        assertMatchingStatuses(statuses, results);     
    }
    
    private void assertMatchingStatuses(JSONObject[] statuses, List<String> results) throws Exception {
        assertEquals(statuses.length, results.size());
        
        for (int i = 0 ; i < statuses.length; i++) {
            JSONObject status = statuses[i];
            JSONObject result = (JSONObject) JSON.parse(results.get(i));
            
            assertEquals(status.get("py_type"), result.get("typeId"));
            assertEquals(status.get("py_device"), result.get("deviceId"));
            
            assertEquals(status.get("py_payload"), result.get("payload"));
        }
    }
    
    public static JSONObject[] generateStatuses(int count) {
        Random r = new Random();
        JSONObject[] statuses = new JSONObject[count];
        
        for (int i = 0 ; i < count ; i++) {
            JSONObject status = new JSONObject();
            
            statuses[i] = status;
            
            status.put("py_type", "T" + r.nextInt(4));
            status.put("py_device", "D" + r.nextInt(10));
            
            JSONObject payload = new JSONObject();
            status.put("py_payload", payload);
            payload.put("s1", (long) r.nextInt(1000));
            payload.put("s2", "S2_" + r.nextInt(5000));
        }
        
        return statuses;
    }

    public Condition<List<String>> completeAndValidate(
            TStream<DeviceStatus> output, int seconds, JSONObject...inputs) throws Exception {
        
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
