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
        log.info("### request : {}", eventRequest.toString());
        EventResponse eventResponse = blockingStub.unaryEvent(eventRequest);
        log.info("### response : {}", eventResponse.toString());
    }

    public void sendAsyncUnaryMessage(EventRequest eventRequest) {
        log.info("### request : {}", eventRequest.toString());
        asyncStub.unaryEvent(eventRequest, new StreamObserver<EventResponse>(){
            @Override
            public void onNext(EventResponse value) {
                log.info("### response : {}", value.toString());
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError");
            }

            @Override
            public void onCompleted() {
                log.info("onCompleted");
            }
        });
        log.info("nonblocking + async 방식이기에 Request 후 Response 전 로그 찍힘");
    }

    public void sendFutureUnaryMessage(EventRequest eventRequest) {
        log.info("### request : {}", eventRequest.toString());
        EventResponse eventResponse = null;
        ListenableFuture<EventResponse> future = futureStub.unaryEvent(eventRequest);
        log.info("Future니까 Nonblocking?");
        try {
            eventResponse = future.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error(e.getMessage());
        }
        log.info("### response : {}", eventResponse.toString());
    }

    public void sendBlockingServerStreamingMessage(EventRequest eventRequest) {
        log.info("### request : {}", eventRequest.toString());
        Iterator<EventResponse> responseIter = blockingStub.serverStreamingEvent(eventRequest);
        responseIter.forEachRemaining(response ->
            log.info("### response : {}", response.toString())
        );
    }

    public void sendAsyncServerStreamingMessage(EventRequest eventRequest) {
        log.info("### request : {}", eventRequest.toString());
        asyncStub.serverStreamingEvent(eventRequest, new StreamObserver<EventResponse>() {
            @Override
            public void onNext(EventResponse value) {
                log.info("### response : {}", value.toString());
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError");
            }

            @Override
            public void onCompleted() {
                log.info("----------[END] Async Server Streaming response----------");
            }
        });
        log.info("서버 응답과 상관없이 다른 작업중..");
    }

    public void sendAsyncClietStreamingMessage(List<EventRequest> eventRequests) {
        StreamObserver<EventResponse> responseObserver = new StreamObserver<EventResponse>() {
            @Override
            public void onNext(EventResponse value) {
                log.info("### response : {}", value.toString());
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError");
            }

            @Override
            public void onCompleted() {
                log.info("----------[END] Async Client Streaming response----------");
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
                log.info("### response : {}", value.toString());
            }

            @Override
            public void onError(Throwable t) {
                log.info("onError");
            }

            @Override
            public void onCompleted() {
                log.info("----------[END] Bidirection Streaming response----------");
            }
        };
        StreamObserver<EventRequest> requestObserver = asyncStub.biStreamingEvent(responseObserver);
        for (EventRequest eventRequest : eventRequests) {
            requestObserver.onNext(eventRequest);
        }
        log.info("async니까 바로 로그 찍힘");
        requestObserver.onCompleted();
    }
}
