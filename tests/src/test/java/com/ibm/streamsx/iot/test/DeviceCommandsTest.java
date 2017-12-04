package com.ibm.streamsx.iot.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import java.io.File;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.iot.DeviceCmd;
import com.ibm.streamsx.iot.IotStreams;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.tester.Condition;
import com.ibm.streamsx.topology.spl.SPL;

public class DeviceCommandsTest {
    
    @Test
    public void testDeviceCmdsAllNoPubFilter() throws Exception {
        testDeviceCmdsAll(false);
    }
    @Test
    public void testDeviceCmdsAllPubFilter() throws Exception {
        testDeviceCmdsAll(true);
    }
    
    private void testDeviceCmdsAll(boolean allowFilter) throws Exception {
        
        Topology topology = new Topology();

        File iotTkLocation = new File(System.getProperty("streamsx.iot.toolkitlocation"));
    	System.out.printf ("IOT ToolkitLocation used in Topology: %s", iotTkLocation);
    	SPL.addToolkit(topology, iotTkLocation);
        
        JSONObject[] commands = generateCommands(200);
        
        Simulate.simulateSentCommands(topology, 10, allowFilter, commands);
        
        TStream<DeviceCmd> cmdStream = IotStreams.commandsSubscribe(topology);
        
        Condition<List<String>> tuples = DeviceEventsTest.completeAndValidate(cmdStream, 30, commands);
        
        List<String> results = tuples.getResult();
        
        assertMatchingCommands(commands, results);     
    }
    
    @Test
    public void testDeviceCmdsSingleNoPubFilter() throws Exception {
        testDeviceCmdsCommandId(false, null, "C3");
    }
    @Test
    public void testDeviceCmdsSinglePubFilter() throws Exception {
        testDeviceCmdsCommandId(true, null, "C1");
    }
    @Test
    public void testDeviceCmdsMultiNoPubFilter() throws Exception {
        testDeviceCmdsCommandId(false, null, "C0", "C4");
    }
    @Test
    public void testDeviceCmdsMultiPubFilter() throws Exception {
        testDeviceCmdsCommandId(true, null, "C2", "C1");
    }
    
    @Test
    public void testDeviceCmdsTypeAndCommandNoPubFilter1() throws Exception {
        testDeviceCmdsCommandId(false, new String[]{"T0", "T3"}, "C0", "C2");
    }
    @Test
    public void testDeviceCmdsTypeAndCommandPubFilter1() throws Exception {
        testDeviceCmdsCommandId(true, new String[]{"T1", "T3", "T2"}, "C2", "C1");
    }
    @Test
    public void testDeviceCmdsTypeAndCommandNoPubFilter2() throws Exception {
        testDeviceCmdsCommandId(false, new String[]{"T0"}, "C0", "C2");
    }
    @Test
    public void testDeviceCmdsTypeAndCommandPubFilter2() throws Exception {
        testDeviceCmdsCommandId(true, new String[]{"T3"}, "C2", "C1");
    }
        
    private void testDeviceCmdsCommandId(boolean allowFilter, String[] typeIds, String...cmdIds) throws Exception {
        
        Topology topology = new Topology();

        File iotTkLocation = new File(System.getProperty("streamsx.iot.toolkitlocation"));
    	System.out.printf ("IOT ToolkitLocation used in Topology: %s", iotTkLocation);
    	SPL.addToolkit(topology, iotTkLocation);
        
        JSONObject[] cmds = generateCommands(200);
        
        Simulate.simulateSentCommands(topology, 10, allowFilter, cmds);
        
        TStream<DeviceCmd> cmdStream = IotStreams.commandsSubscribe(topology, typeIds, cmdIds);
        
        Set<String> ftypes = new HashSet<>();
        if (typeIds != null)
            ftypes.addAll(Arrays.asList(typeIds));
        
        Set<String> fcmds = new HashSet<>();
        fcmds.addAll(Arrays.asList(cmdIds));
        
        // must be filtering on something
        assertFalse(ftypes.isEmpty() && fcmds.isEmpty());
        
        List<JSONObject> filteredCommandsL = new ArrayList<>();
        for (JSONObject cmd : cmds) {
            String tid = (String) cmd.get("py_type");
            String cid = (String) cmd.get("py_command");
            
            // just filtering on commands
            if (ftypes.isEmpty()) {
                if (fcmds.contains(cid))
                    filteredCommandsL.add(cmd);
                continue;
            }
            
            // just filtering on types
            if (fcmds.isEmpty()) {
                if (ftypes.contains(tid))
                    filteredCommandsL.add(cmd);
                continue;
            }
            
            // filtering on both
            if (ftypes.contains(tid) && fcmds.contains(cid))
                filteredCommandsL.add(cmd);
        }
        cmds = filteredCommandsL.toArray(new JSONObject[filteredCommandsL.size()]);
        
        Condition<List<String>> tuples = DeviceEventsTest.completeAndValidate(cmdStream, 30, cmds);
        
        List<String> results = tuples.getResult();
        
        assertMatchingCommands(cmds, results);     
    }
    
    private void assertMatchingCommands(JSONObject[] cmds, List<String> results) throws Exception {
        assertEquals(cmds.length, results.size());
        
        for (int i = 0 ; i < cmds.length; i++) {
            JSONObject cmd = cmds[i];
            JSONObject result = (JSONObject) JSON.parse(results.get(i));
            
            assertEquals(cmd.get("py_type"), result.get("typeId"));
            assertEquals(cmd.get("py_device"), result.get("deviceId"));
            assertEquals(cmd.get("py_command"), result.get("cmdId"));
            
            assertEquals(cmd.get("py_data"), result.get("d"));
            
            if (cmd.containsKey("py_ts")) {
                assertTrue(result.containsKey("ts"));
                Instant expectedTs = Instant.ofEpochMilli((Long) cmd.get("py_ts"));
                assertEquals(expectedTs, Instant.parse((String) result.get("ts")));
            } else {
                assertFalse(result.containsKey("ts"));
            }
        }
    }
    
    public static JSONObject[] generateCommands(int count) {
        Random r = new Random();
        JSONObject[] commands = new JSONObject[count];
        
        for (int i = 0 ; i < count ; i++) {
            JSONObject cmd = new JSONObject();
            
            commands[i] = cmd;
            
            cmd.put("py_type", "T" + r.nextInt(4));
            cmd.put("py_device", "D" + r.nextInt(10));
            cmd.put("py_command", "C" + r.nextInt(5));
            
            if (r.nextFloat() > 0.1) {
                int offset = r.nextInt(20000) - 10000;
                cmd.put("py_ts", System.currentTimeMillis() + offset);
            }
            
            JSONObject data = new JSONObject();
            cmd.put("py_data", data);
            data.put("c1", (long) r.nextInt(1000));
            data.put("c2", "C2_" + r.nextInt(5000));
        }
        
        return commands;
    }
}
