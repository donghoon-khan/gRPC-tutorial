package com.donghoonkhan.grpc.client;

import com.donghoonkhan.grpc.proto.NewdataServiceGrpc;
import com.donghoonkhan.grpc.proto.NewdataServiceGrpc.NewdataServiceBlockingStub;
import com.donghoonkhan.grpc.proto.NewdataServiceGrpc.NewdataServiceFutureStub;
import com.donghoonkhan.grpc.proto.NewdataServiceGrpc.NewdataServiceStub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class NewDataClientStubFactory {
    
    private final ManagedChannel channel;
    private final NewdataServiceBlockingStub blockingStub;
    private final NewdataServiceStub asyncStub;
    private final NewdataServiceFutureStub futureStub;

    public NewDataClientStubFactory(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                        .usePlaintext()
                        .build();
        this.blockingStub = NewdataServiceGrpc.newBlockingStub(channel);
        this.asyncStub = NewdataServiceGrpc.newStub(channel);
        this.futureStub = NewdataServiceGrpc.newFutureStub(channel);
    }

    public NewdataServiceBlockingStub getBlockingStub() {
        return blockingStub;
    }

    public NewdataServiceStub getAsyncStub() {
        return asyncStub;
    }

    public NewdataServiceFutureStub getFutureStub() {
        return futureStub;
    }

}
