package com.donghoonkhan.grpc.server.service;

import com.donghoonkhan.grpc.proto.EventRequest;
import com.donghoonkhan.grpc.proto.EventResponse;
import com.donghoonkhan.grpc.proto.NewdataServiceGrpc.NewdataServiceImplBase;

import org.springframework.stereotype.Service;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NewDataServiceImpl extends NewdataServiceImplBase {

    @Override
    public void unaryEvent(EventRequest request, StreamObserver<EventResponse> responseObserver) {
        log.info("### unary Stream : {}", request.toString());

        EventResponse eventResponse = EventResponse.newBuilder()
                .setResult(request.getSourceId() + request.getEventId()).build();
        
        responseObserver.onNext(eventResponse);
        log.info("onNext");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        responseObserver.onCompleted();
        log.info("onCompleted");
    }

    @Override 
    public void serverStreamingEvent(EventRequest request, StreamObserver<EventResponse> responseObserver) { 
        log.info("### Server Stream : {}", request.toString());

        EventResponse eventResponse = EventResponse.newBuilder() 
                .setResult(request.getSourceId() + request.getEventId()) .build();
        responseObserver.onNext(eventResponse); 
        responseObserver.onNext(eventResponse); 
        responseObserver.onNext(eventResponse); 
        responseObserver.onNext(eventResponse); 
        try { 
            Thread.sleep(1000); 
        } catch (InterruptedException e) { 
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        } 
        responseObserver.onCompleted(); 
    }
    @Override 
    public StreamObserver<EventRequest> clientStreamingEvent(StreamObserver<EventResponse> responseObserver) { 
        return new StreamObserver<EventRequest>() {

            @Override
			public void onNext(EventRequest value) {
                log.info("### Client Stream : {}", value.toString());
			}

            @Override
			public void onError(Throwable t) {
				log.info("onError");
			}

            @Override
			public void onCompleted() {
                EventResponse response = EventResponse.newBuilder()
                        .setResult("response")
                        .build();
                responseObserver.onNext(response);
                responseObserver.onCompleted();
			}
        };
    }

    @Override
    public StreamObserver<EventRequest> biStreamingEvent(StreamObserver<EventResponse> responseObserver) {
        return new StreamObserver<EventRequest>() { 
            @Override 
            public void onNext(EventRequest value) { 
                log.info("Bidirection" + value.getSourceId() + "/" + value.getEventId()); 
                responseObserver.onNext(EventResponse.newBuilder().setResult("응답1").build()); 
                responseObserver.onNext(EventResponse.newBuilder().setResult("응답2").build()); 
                responseObserver.onNext(EventResponse.newBuilder().setResult("응답3").build()); 
            }

            @Override 
            public void onError(Throwable t) { 
                log.info("onError"); 
            }

            @Override 
            public void onCompleted() { 
                log.info("onCompleted"); 
                responseObserver.onCompleted(); 
            } 
        };
    }
}
