/*
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2016 
 */
package com.ibm.streamsx.iotf.sample;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.ibm.streamsx.iotf.DeviceEvent;
import com.ibm.streamsx.iotf.IotfStreams;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.TopologyElement;
import com.ibm.streamsx.topology.context.AnalyticsServiceProperties;
import com.ibm.streamsx.topology.context.StreamsContext.Type;
import com.ibm.streamsx.topology.context.StreamsContextFactory;

/**
 * Sample application subscribing to device events.
 * 
 * @see com.ibm.streamsx.iotf.IotfStreams#eventsSubscribe(TopologyElement, String...)
 */
public class Events {

    private Events() {}
    
    /**
     * Execute this application against a Bluemix Streaming Analytic Service.
     * 
     * Usage:
     * <BR>
     * {@code java com.ibm.streamsx.iotf.sample.Events vcapFile serviceName [eventId ...]}
     * @param args Arguments to the application.
     * @throws Exception Exception executing 
     */
    public static void main(String[] args) throws Exception {
        
       
        String vcapFile = args[0];
        String serviceName = args[1];
        String[] eventIds = null;
        if (args.length > 2) {
            eventIds = new String[args.length - 2];
            System.arraycopy(args, 2, eventIds, 0, args.length - 2);
        }
        
        // The declared application.
        Topology topology = new Topology();
        
        // Subscribe to device events and just print them to standard out.
        TStream<DeviceEvent> events = IotfStreams.eventsSubscribe(topology, eventIds);        
        events.print();
        
        // Submit to BlueMix
        Map<String,Object> config = new HashMap<>();      
        config.put(AnalyticsServiceProperties.VCAP_SERVICES, new File(vcapFile));
        config.put(AnalyticsServiceProperties.SERVICE_NAME, serviceName);
        StreamsContextFactory.getStreamsContext(Type.ANALYTICS_SERVICE).submit(topology, config);
    }
}
