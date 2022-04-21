package com.ing.chapter.demo.playground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class BackPressureTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackPressureTest.class);

    @Test
    void whenRequestingChunks10_thenMessagesAreReceived() {
        Flux<Integer> request = Flux.range(1, 50).log();

        System.out.println("\n***** Subscribe without backpressure ****");
        request.subscribe();

        System.out.println("\n***** Subscribe with backpressure ****");
        request.subscribe(
                integer -> {},
                Throwable::printStackTrace,
                () -> LOGGER.debug("All 50 items have been successfully processed!!!"),
                subscription -> {
                    for (int i = 0; i < 5; i++) {
                        LOGGER.debug("Requesting the next 10 elements!!!");
                        subscription.request(10);
                    }
                }
        );

        /*System.out.println("\n***** hookOnSubscribe with backpressure ****");
        request.subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                subscription.request(10);
            }
        });*/

        StepVerifier.create(request)
                .expectSubscription()
                .thenRequest(10)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .thenRequest(10)
                .expectNext(11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
                .thenRequest(10)
                .expectNext(21, 22, 23, 24, 25, 26, 27 , 28, 29 ,30)
                .thenRequest(10)
                .expectNext(31, 32, 33, 34, 35, 36, 37 , 38, 39 ,40)
                .thenRequest(10)
                .expectNext(41, 42, 43, 44, 45, 46, 47 , 48, 49 ,50)
                .verifyComplete();
    }

    @Test
    void whenLimitRateSet_thenSplitIntoChunks() throws InterruptedException {
        Flux<Integer> limit = Flux.range(1, 25)
                .log()
                .limitRate(10); //75% of prefetchRate (10)

        System.out.println("\n***** Subscribe without backpressure ****");
        limit.subscribe();

        System.out.println("\n***** Subscribe with backpressure ****");
        limit.subscribe(
                new BaseSubscriber<>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        subscription.request(10);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println("Processing value : " + value);
                    }
                }
        );

        /*StepVerifier.create(limit)
                .expectSubscription()
                .thenRequest(15)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .expectNext( 11,12, 13, 14, 15)
                .thenRequest(10)
                .expectNext(16, 17, 18, 19, 20, 21, 22, 23, 24, 25)
                .verifyComplete();*/
    }

    @Test
    void whenCancel_thenSubscriptionFinished() {
        Flux<Integer> cancel = Flux.range(1, 10).log();

        cancel.subscribe(new BaseSubscriber<>() {

            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                subscription.request(3);
                cancel();
            }

        });

        /*StepVerifier.create(cancel)
                .expectNext(1, 2, 3)
                .thenCancel()
                .verify();*/
    }
}
