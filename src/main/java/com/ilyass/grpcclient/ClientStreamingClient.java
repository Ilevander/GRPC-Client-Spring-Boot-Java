package com.ilyass.grpcclient;

import com.ilyass.grpcserver.Calculator;
import com.ilyass.grpcserver.CalculatorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ClientStreamingClient {
    public static void main(String[] args) throws IOException {
        ManagedChannel channel = ManagedChannelBuilder.
                forAddress("localhost", 9999).
                usePlaintext().
                build();

        CalculatorServiceGrpc.CalculatorServiceStub asyncStub = CalculatorServiceGrpc.newStub(channel);

        StreamObserver<Calculator.ClientStreamRequest> performStream = asyncStub.performStream(new StreamObserver<Calculator.ClientStreamResponse>() {
            @Override
            public void onNext(Calculator.ClientStreamResponse clientStreamResponse) {
                System.out.println(clientStreamResponse);
            }

            @Override
            public void onError(Throwable throwable) {
            }

            @Override
            public void onCompleted() {
                System.out.println("END");
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int counter = 0;

            @Override
            public void run() {
                Calculator.ClientStreamRequest clientStreamRequest = Calculator.
                        ClientStreamRequest.
                        newBuilder().
                        setA(Math.random() * 10).
                        build();
                performStream.onNext(clientStreamRequest);
                counter++;
                if (counter == 10) {
                    performStream.onNext(clientStreamRequest);
                    performStream.onCompleted();
                    timer.cancel();
                }
            }

        }, 1000, 1000);
        System.out.println("...");
        System.in.read();
    }
}
