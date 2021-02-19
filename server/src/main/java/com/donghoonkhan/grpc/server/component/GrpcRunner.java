package com.donghoonkhan.grpc.server.component;

import com.donghoonkhan.grpc.server.service.NewDataServiceImpl;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GrpcRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        int port = 9595;

        Server server = ServerBuilder.forPort(port)
                .addService(new NewDataServiceImpl())
                .build();
        server.start();
        log.info("gRPC server listening on port [" + port + "]");
        server.awaitTermination();
    }
}
