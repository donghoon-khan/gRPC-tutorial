package com.donghoonkhan.grpc.client;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.donghoonkhan.grpc.proto.EventRequest;

import org.springframework.stereotype.Service;

@Service
public class ClientService {
    
    @PostConstruct
    private void init() {
        String host = "localhost";
        int port = 9595;

        NewDataClientStubFactory newDataClientStubFactory = new NewDataClientStubFactory(host, port);
        NewDataClient newDataClient = new NewDataClient(newDataClientStubFactory.getBlockingStub(), 
                newDataClientStubFactory.getAsyncStub(), newDataClientStubFactory.getFutureStub());
        
        EventRequest eventRequest1 = EventRequest.newBuilder()
                .setSourceId("sourceId1")
                .setEventId("eventId1")
                .build();
        
        EventRequest eventRequest2 = EventRequest.newBuilder()
        .setSourceId("sourceId2")
        .setEventId("eventId2")
        .build();

        EventRequest eventRequest3 = EventRequest.newBuilder()
        .setSourceId("sourceId3")
        .setEventId("eventId3")
        .build();

        List<EventRequest> eventRequests = new ArrayList<>();
        eventRequests.add(eventRequest1);
        eventRequests.add(eventRequest2);
        eventRequests.add(eventRequest3);

        newDataClient.sendBlockingUnaryMessage(eventRequest1);
        newDataClient.sendBiDirectionalStreamingMessage(eventRequests);
    }

}
