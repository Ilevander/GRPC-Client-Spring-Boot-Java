package com.ilyass.grpcclient;

import com.ilyass.grpcserver.Calculator;
import com.ilyass.grpcserver.CalculatorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class ServerStreamingClient {
    public static void main(String[] args) throws IOException {
        ManagedChannel channel = ManagedChannelBuilder.
                forAddress("localhost", 9999).
                usePlaintext().
                build();
        double a = 10d;
        double b = 20d;
        Calculator.ServerStreamRequest request = Calculator.ServerStreamRequest.newBuilder()
                .setA(a)
                .setB(b).
                build();
        CalculatorServiceGrpc.CalculatorServiceStub asynStub = CalculatorServiceGrpc.newStub(channel);

        asynStub.getOperationStream(request, new StreamObserver<Calculator.ServerStreamResponse>() {

            @Override
            public void onNext(Calculator.ServerStreamResponse serverStreamResponse) {
                System.out.println("#######");
                System.out.println(serverStreamResponse);
                System.out.println("#######");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("FIN...");
            }
        });

        System.out.println("Waiting data ...");
        System.in.read();
    }
}
