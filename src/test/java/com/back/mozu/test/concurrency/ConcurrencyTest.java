package com.back.mozu.test.concurrency;

import static org.assertj.core.api.Assertions.assertThat;

import com.back.mozu.domain.queue.service.LockService;
import com.back.mozu.domain.reservation.entity.TimeSlot;
import com.back.mozu.domain.reservation.repository.TimeSlotRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/moju_db?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true",
        "spring.data.redis.host=localhost"
})
public class ConcurrencyTest {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private LockService lockService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private UUID timeSlotId;
    private final int THREAD_COUNT = 32;
    private final int REQUEST_COUNT = 1000;
    private final int INITIAL_STOCK = 1000;

    @BeforeEach
    void setUp() {
        TimeSlot slot = TimeSlot.builder()
                .date(LocalDate.now())
                .time(LocalTime.of(12, 0))
                .stock(INITIAL_STOCK)
                .build();
        timeSlotId = timeSlotRepository.save(slot).getId();
    }

    @AfterEach
    void tearDown() {
        timeSlotRepository.deleteAllInBatch();
    }

    // 낙관적 락
    private void decreaseWithOptimisticLock() throws InterruptedException {
        while (true) {
            try {
                transactionTemplate.executeWithoutResult(status -> {
                    TimeSlot slot = timeSlotRepository.findById(timeSlotId).orElseThrow();
                    slot.occupy(1);
                });
                break;
            } catch (ObjectOptimisticLockingFailureException e) {
                Thread.sleep(50);
            }
        }
    }


    // Redis 분산 락
    private void decreaseWithCustomRedisLock() throws InterruptedException {
        String token = UUID.randomUUID().toString();
        String timeSlotIdStr = timeSlotId.toString();

        while (true) {
            // 락 획득 시도
            if (lockService.acquireLock(timeSlotIdStr, token)) {
                try {
                    transactionTemplate.executeWithoutResult(status -> {
                        TimeSlot slot = timeSlotRepository.findById(timeSlotId).orElseThrow();
                        slot.occupy(1);
                    });
                    break;
                } finally {
                    lockService.releaseLock(timeSlotIdStr, token);
                }
            } else {
                Thread.sleep(50);
            }
        }
    }

    @Test
    @DisplayName("낙관적 락 vs Custom Redis 분산 락 성능 비교")
    void comparePerformance() throws InterruptedException {
        StopWatch stopWatch = new StopWatch("동시성 제어 락(Lock) 성능 비교");

        // 낙관적 락
        ExecutorService executor1 = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch1 = new CountDownLatch(REQUEST_COUNT);

        stopWatch.start("1. 낙관적 락");
        for (int i = 0; i < REQUEST_COUNT; i++) {
            executor1.execute(() -> {
                try {
                    decreaseWithOptimisticLock();
                } catch (Exception e) {
                } finally {
                    latch1.countDown();
                }
            });
        }
        latch1.await();
        stopWatch.stop();
        executor1.shutdown();

        TimeSlot optResult = timeSlotRepository.findById(timeSlotId).orElseThrow();
        assertThat(optResult.getStock()).isEqualTo(0);

        timeSlotRepository.deleteAllInBatch();
        TimeSlot newSlot = TimeSlot.builder().date(LocalDate.now()).time(LocalTime.of(12, 0)).stock(INITIAL_STOCK).build();
        timeSlotId = timeSlotRepository.save(newSlot).getId();

        // Redis 분산 락
        ExecutorService executor2 = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch2 = new CountDownLatch(REQUEST_COUNT);

        stopWatch.start("2. Redis 분산 락");
        for (int i = 0; i < REQUEST_COUNT; i++) {
            executor2.execute(() -> {
                try {
                    decreaseWithCustomRedisLock();
                } catch (Exception e) {
                } finally {
                    latch2.countDown();
                }
            });
        }
        latch2.await();
        stopWatch.stop();
        executor2.shutdown();

        TimeSlot redisResult = timeSlotRepository.findById(timeSlotId).orElseThrow();
        assertThat(redisResult.getStock()).isEqualTo(0);

        System.out.println("=============================================");
        System.out.println(stopWatch.prettyPrint());
        System.out.println("=============================================");
    }
}