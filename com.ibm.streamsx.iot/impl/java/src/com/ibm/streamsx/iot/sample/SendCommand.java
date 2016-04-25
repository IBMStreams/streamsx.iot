/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iot.sample;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.ibm.json.java.JSONObject;
import com.ibm.streamsx.iot.Device;
import com.ibm.streamsx.iot.DeviceCmd;
import com.ibm.streamsx.iot.IotfStreams;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.TopologyElement;
import com.ibm.streamsx.topology.context.AnalyticsServiceProperties;
import com.ibm.streamsx.topology.context.StreamsContext.Type;
import com.ibm.streamsx.topology.context.StreamsContextFactory;

/**
 * Sample application subscribing to device commands.
 * 
 * @see com.ibm.streamsx.iot.IotfStreams#commandsSubscribe(TopologyElement, String...)
 */
public class SendCommand {

    private SendCommand() {}
    
    /**
     * Execute this application against a Bluemix Streaming Analytic Service.
     * 
     * Usage:
     * <BR>
     * {@code java com.ibm.streamsx.iotf.sample.SendCommand vcapFile serviceName typeId deviceId message}
     * @param args Arguments to the application.
     * @throws Exception Exception executing 
     */
    public static void main(String[] args) throws Exception {
      
        String vcapFile = args[0];
        String serviceName = args[1];
        String typeId = args[2];
        String deviceId = args[3];
        String message = args[4];
        
        // The declared application.
        Topology topology = new Topology();
        
        final Device device = new Device(typeId, deviceId);
        
        TStream<DeviceCmd> command = topology.periodicSource(() -> {
            JSONObject payload = new JSONObject();
            payload.put("msg", message + " @ " + new Date().toString());
            return new DeviceCmd(device, "display", payload);
        }, 10, SECONDS);
        
        IotfStreams.commandPublish(command);
        
        // Submit to BlueMix
        Map<String,Object> config = new HashMap<>();      
        config.put(AnalyticsServiceProperties.VCAP_SERVICES, new File(vcapFile));
        config.put(AnalyticsServiceProperties.SERVICE_NAME, serviceName);
        StreamsContextFactory.getStreamsContext(Type.ANALYTICS_SERVICE).submit(topology, config);
    }
    

}
