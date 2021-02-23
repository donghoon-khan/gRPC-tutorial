package com.donghoonkhan.grpc.client;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.donghoonkhan.grpc.proto.EventRequest;
import com.donghoonkhan.grpc.proto.EventResponse;
import com.donghoonkhan.grpc.proto.NewdataServiceGrpc.NewdataServiceBlockingStub;
import com.donghoonkhan.grpc.proto.NewdataServiceGrpc.NewdataServiceFutureStub;
import com.donghoonkhan.grpc.proto.NewdataServiceGrpc.NewdataServiceStub;
import com.google.common.util.concurrent.ListenableFuture;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class NewDataClient {
    private final NewdataServiceBlockingStub blockingStub;
    private final NewdataServiceStub asyncStub;
    private final NewdataServiceFutureStub futureStub;


    public void sendBlockingUnaryMessage(EventRequest eventRequest) {
        log.info("### BlockingUnaryRequest : {}", eventRequest.toString());
        EventResponse eventResponse = blockingStub.unaryEvent(eventRequest);
        log.info("### BlockingUnaryResponse : {}", eventResponse.toString());
    }

    public void sendAsyncUnaryMessage(EventRequest eventRequest) {
        log.info("### AsyncUnaryRequest : {}", eventRequest.toString());
        asyncStub.unaryEvent(eventRequest, new StreamObserver<EventResponse>(){
            @Override
            public void onNext(EventResponse value) {
                log.info("### AsyncUnaryResponse : {}", value.toString());
            }

            @Override
            public void onError(Throwable t) {
                log.info("AsyncUnaryOnError");
            }

            @Override
            public void onCompleted() {
                log.info("AsyncUnaryOnCompleted");
            }
        });
        log.info("AsyncUnaryDone");
    }

    public void sendFutureUnaryMessage(EventRequest eventRequest) {
        log.info("### FutureUnaryRequest : {}", eventRequest.toString());
        EventResponse eventResponse = null;
        ListenableFuture<EventResponse> future = futureStub.unaryEvent(eventRequest);
        try {
            eventResponse = future.get(2, TimeUnit.SECONDS);
            log.info("### FutureUnaryResponse : {}", eventResponse.toString());
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage());
        }
    }

    public void sendBlockingServerStreamingMessage(EventRequest eventRequest) {
        log.info("### BlockingServerStreamingRequest : {}", eventRequest.toString());
        Iterator<EventResponse> responseIter = blockingStub.serverStreamingEvent(eventRequest);
        responseIter.forEachRemaining(response ->
            log.info("### BlockingServerStreamingResponse : {}", response.toString())
        );
    }

    public void sendAsyncServerStreamingMessage(EventRequest eventRequest) {
        log.info("### AsyncServerStreamingRequest : {}", eventRequest.toString());
        asyncStub.serverStreamingEvent(eventRequest, new StreamObserver<EventResponse>() {
            @Override
            public void onNext(EventResponse value) {
                log.info("### AsyncServerStreamingResponse : {}", value.toString());
            }

            @Override
            public void onError(Throwable t) {
                log.info("AsyncServerStreamingOnError");
            }

            @Override
            public void onCompleted() {
                log.info("AsyncServerStreamingOnCompleted");
            }
        });
        log.info("AsyncServerStreamingDone");
    }

    public void sendAsyncClietStreamingMessage(List<EventRequest> eventRequests) {
        StreamObserver<EventResponse> responseObserver = new StreamObserver<EventResponse>() {
            @Override
            public void onNext(EventResponse value) {
                log.info("### AsyncClientStreamingResponse : {}", value.toString());
            }

            @Override
            public void onError(Throwable t) {
                log.info("AsyncClientStreamingOnError");
            }

            @Override
            public void onCompleted() {
                log.info("AsyncClientStreamingOnCompleted");
            }
        };

        StreamObserver<EventRequest> requestObserver = asyncStub.clientStreamingEvent(responseObserver);
        for (EventRequest eventRequest : eventRequests) {
            requestObserver.onNext(eventRequest);
        }
        requestObserver.onCompleted();
    }

    public void sendBiDirectionalStreamingMessage(List<EventRequest> eventRequests) {
        StreamObserver<EventResponse> responseObserver = new StreamObserver<EventResponse>() {
            @Override
            public void onNext(EventResponse value) {
                log.info("### BiDirectionalStreamingResponse : {}", value.toString());
            }

            @Override
            public void onError(Throwable t) {
                log.info("BiDirectionalStreamingOnError");
            }

            @Override
            public void onCompleted() {
                log.info("BiDirectionalStreamingOnCompleted");
            }
        };
        StreamObserver<EventRequest> requestObserver = asyncStub.biStreamingEvent(responseObserver);
        for (EventRequest eventRequest : eventRequests) {
            requestObserver.onNext(eventRequest);
        }
        requestObserver.onCompleted();
    }
}
