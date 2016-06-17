package com.ibm.streamsx.iot.test.watson;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.iot.Device;
import com.ibm.streamsx.iot.DeviceCmd;
import com.ibm.streamsx.iot.IotStreams;
import com.ibm.streamsx.iot.test.Simulate.Delay;
import com.ibm.streamsx.topology.Topology;

import quarks.connectors.iot.IotDevice;
import quarks.connectors.iot.QoS;
import quarks.connectors.iotf.IotfDevice;
import quarks.execution.Job;
import quarks.providers.direct.DirectProvider;
import quarks.providers.direct.DirectTopology;
import quarks.topology.TStream;
import quarks.topology.plumbing.PlumbingStreams;

public class Edgent2Watson {
    
    public static AtomicBoolean FAILED = new AtomicBoolean();
    
    /**
     * Send a command to tell the Quarks client to release the events!
     */
    public static void go(Topology topology) {
        Device device = new Device("Test", "Test001");
        DeviceCmd goCmd = new DeviceCmd(device, "go", null, new JSONObject());
        com.ibm.streamsx.topology.TStream<DeviceCmd> cmds =
                 topology.constants(Collections.singletonList(goCmd)).asType(DeviceCmd.class);
        cmds = cmds.modify(new Delay<>(5));
        IotStreams.commandPublish(cmds);
    }
    
    // Create a SPL stream that looks like device events
    public static void createEvents(JSONObject ...payloads) throws Exception {
        
        assertFalse(FAILED.get());
        
        String deviceCfg = System.getProperty("streamsx.iot.test.device.cfg");
        assertNotNull(deviceCfg);
        assumeTrue(!"skip".equals(deviceCfg));
        
        File dcf = new File(deviceCfg);
        assertTrue(dcf.exists());
        assertTrue(dcf.isFile());
         
        new Thread(() -> { 
            try {
                buildAndRunEdgesApp(deviceCfg, payloads);
            } catch (Exception e) {
                e.printStackTrace();
                FAILED.set(true);
            }
        }).start();
    }
    
    private static void buildAndRunEdgesApp(String deviceCfg, JSONObject ...payloads) throws Exception{
        
        DirectProvider tp = new DirectProvider();
        DirectTopology topology = tp.newTopology("Edgent2WatsonEvents");
        
        AtomicBoolean go = new AtomicBoolean();

        // Declare a connection to IoTF
        IotDevice device = new IotfDevice(topology, new File(deviceCfg));
        
        TStream<JsonObject> cmds = device.commands("go");
        cmds.print();
        cmds.sink(e -> go.set(true));
        
        TStream<JsonObject> cmds2 = device.commands();
        cmds2.print();
       
        TStream<JSONObject> pys = topology.collection(Arrays.asList(payloads));
                
        JsonParser jp = new JsonParser();
                
        TStream<JsonObject> pysj;
        pysj = pys.map(ij -> {
            try {
                while (!go.get()) {
                    System.out.println("GO:" + go.get());
                    Thread.sleep(1000);
                }
                return jp.parse(ij.serialize()).getAsJsonObject();
            } catch (Exception ioe) {
                ioe.printStackTrace();
                FAILED.set(true);
                throw new RuntimeException(ioe);

            }

        });    
        
        pysj = PlumbingStreams.blockingThrottle(pysj, 50, TimeUnit.MILLISECONDS);
        
        device.events(pysj,
                e -> e.getAsJsonPrimitive("py_event").getAsString(),
                e -> e.getAsJsonObject("py_data"), e -> QoS.FIRE_AND_FORGET);
        
        // Heartbeat
        TStream<JsonObject> hb = topology.poll(JsonObject::new, 1, TimeUnit.SECONDS);
        device.events(hb, "heartbeat", 0);
        
        Job job = tp.submit(topology).get();
        
        job.complete();
              
    }
}
