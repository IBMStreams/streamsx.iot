package com.ibm.streamsx.iot.test.watson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.ibm.json.java.JSON;
import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.iot.Device;
import com.ibm.streamsx.iot.DeviceCmd;
import com.ibm.streamsx.iot.IotStreams;
import com.ibm.streamsx.iot.test.DeviceCommandsTest;
import com.ibm.streamsx.iot.test.DeviceEventsTest;
import com.ibm.streamsx.iot.test.Simulate;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.tester.Condition;

public class WatsonDeviceCommandsTest {
    
    @Test
    public void testDeviceCmdsAll() throws Exception {
                
        Topology topology = new Topology();
        
        JSONObject[] commands = DeviceCommandsTest.generateCommands(20);
        
        TStream<JSONObject> rawCmds = topology.constants(Arrays.asList(commands));
        rawCmds = rawCmds.modify(new Simulate.Delay<>(5));
        
        final Device testDevice = new Device("Test", "Test001");
        
        TStream<DeviceCmd> cookedCmds = rawCmds.transform(
                j -> new DeviceCmd(testDevice, j.get("py_command").toString(),
                        null, (JSONObject) j.get("py_data"))).asType(DeviceCmd.class);
        cookedCmds = cookedCmds.throttle(50, TimeUnit.MILLISECONDS);
        IotStreams.commandPublish(cookedCmds);
                
        TStream<DeviceCmd> cmdStream = IotStreams.commandsSubscribe(topology);
        
        Condition<List<String>> tuples = DeviceEventsTest.completeAndValidate(cmdStream, 30, commands);
        
        List<String> results = tuples.getResult();
        
        assertMatchingCommands(commands, results);     
    }
    
    @Test
    public void testDeviceCmdsFilter() throws Exception {
                
        Topology topology = new Topology();
        
        JSONObject[] commands = DeviceCommandsTest.generateCommands(40);
        
        TStream<JSONObject> rawCmds = topology.constants(Arrays.asList(commands));
        rawCmds = rawCmds.modify(new Simulate.Delay<>(5));
        
        final Device testDevice = new Device("Test", "Test001");
        
        TStream<DeviceCmd> cookedCmds = rawCmds.transform(
                j -> new DeviceCmd(testDevice, j.get("py_command").toString(),
                        null, (JSONObject) j.get("py_data"))).asType(DeviceCmd.class);
        
        cookedCmds = cookedCmds.throttle(50, TimeUnit.MILLISECONDS);
        
        IotStreams.commandPublish(cookedCmds);
        
        String filterCmdId = commands[5].get("py_command").toString();
        List<JSONObject> filteredCommands = new ArrayList<>();
        for (JSONObject cmd : commands) {
            if (filterCmdId.equals(cmd.get("py_command"))) {
                filteredCommands.add(cmd);
            }      
        }
        assertFalse(filteredCommands.isEmpty());
        JSONObject[] fca = filteredCommands.toArray(new JSONObject[filteredCommands.size()]);

                
        TStream<DeviceCmd> cmdStream = IotStreams.commandsSubscribe(topology, filterCmdId);
        
        Condition<List<String>> tuples = DeviceEventsTest.completeAndValidate(cmdStream, 30, fca);
        
        List<String> results = tuples.getResult();
        
        assertMatchingCommands(fca, results);     
    }
    
    private void assertMatchingCommands(JSONObject[] cmds, List<String> results) throws Exception {
        assertEquals(cmds.length, results.size());
        
        for (int i = 0 ; i < cmds.length; i++) {
            JSONObject cmd = cmds[i];
            JSONObject result = (JSONObject) JSON.parse(results.get(i));
            
            System.err.println("CMD   :" + cmd);
            System.err.println("RESULT:" + result);
            
            assertEquals(cmd.get("py_command"), result.get("cmdId"));
            
            assertEquals(cmd.get("py_data"), result.get("d"));
            
            assertTrue(result.containsKey("ts"));
        }
    }
}
