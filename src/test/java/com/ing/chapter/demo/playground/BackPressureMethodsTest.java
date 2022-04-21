package com.ing.chapter.demo.playground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;

class BackPressureMethodsTest {

    @Test
    void onBackpressureBufferTest() {
        Flux<Integer> request = Flux.range(1, 50)
                .log()
                .onBackpressureBuffer(15, e -> System.out.println("Buffer overflow at: " + e));

        System.out.println("\n***** Subscribe without backpressure ****");
        request.subscribe();

        System.out.println("\n***** Subscribe with backpressure ****");
        request.subscribe(new BaseSubscriber<>() {
                              @Override
                              protected void hookOnSubscribe(Subscription subscription) {
                                  subscription.request(5);
                              }

                              @Override
                              protected void hookOnNext(Integer value) {
                                  System.out.println("Processing value : " + value);
                                  if(value == 5){
                                      request(10);
                                  }
                              }
                          }
        );
    }

    @Test
    void onBackpressureDropTest() {
        Flux<Integer> request = Flux.range(1, 50)
                .log()
                .onBackpressureDrop(e -> System.out.println("Dropped: " + e));

        System.out.println("\n***** Subscribe without backpressure ****");
        request.subscribe();

        System.out.println("\n***** Subscribe with backpressure ****");
        request.subscribe(new BaseSubscriber<>() {
                              @Override
                              protected void hookOnSubscribe(Subscription subscription) {
                                  subscription.request(5);
                              }

                              @Override
                              protected void hookOnNext(Integer value) {
                                  System.out.println("Processing value : " + value);
                              }
                          }
        );
    }


    @Test
    void onBackpressureErrorTest() {
        Flux<Integer> request = Flux.range(1, 10)
                .log()
                .onBackpressureError();

        System.out.println("\n***** Subscribe without backpressure ****");
        request.subscribe();

        System.out.println("\n***** Subscribe with backpressure ****");
        request.subscribe(new BaseSubscriber<>() {
                              @Override
                              protected void hookOnSubscribe(Subscription subscription) {
                                  subscription.request(5);
                              }

                              @Override
                              protected void hookOnNext(Integer value) {
                                  System.out.println("Processing value : " + value);
                              }
                          }
        );
    }

}
