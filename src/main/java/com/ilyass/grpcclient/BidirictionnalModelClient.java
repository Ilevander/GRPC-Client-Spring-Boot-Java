package com.ilyass.grpcclient;

import com.ilyass.grpcserver.Calculator;
import com.ilyass.grpcserver.CalculatorServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Timer;
import java.util.TimerTask;

public class BidirictionnalModelClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.
                forAddress("localhost", 9999)
                .usePlaintext().
                build();

        CalculatorServiceGrpc.CalculatorServiceStub aysncStub = CalculatorServiceGrpc.newStub(channel);
        StreamObserver fullStream = aysncStub.fullStream(new StreamObserver<Calculator.BidirectionalStreamResponse>() {
            @Override
            public void onNext(Calculator.BidirectionalStreamResponse operationResponse) {
                System.out.println(operationResponse);
                System.out.println("########");
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.println("FIN");
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int counter = 0;

            @Override
            public void run() {
                Calculator.BidirectionalStreamRequest bidirectionalStreamRequest = Calculator.
                        BidirectionalStreamRequest.
                        newBuilder().
                        setA(Math.random() * 100).
                        build();
                fullStream.onNext(bidirectionalStreamRequest);
                counter++;
                System.out.println("counter=" + counter);
                if (counter == 10) {
                    fullStream.onCompleted();
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }
}
