package edu.ku.eecs690.simulation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * required to run a monte-carlo simulation
 */
public class BlackJackSimulation implements Simulation {

    /**
     * total simulation to run
     */
    private final Long sims;

    /**
     * to run on
     */
    private final Integer threads;

    public BlackJackSimulation(Long sims, Integer threads) {
        this.sims = sims;
        this.threads = threads;
    }

    @Override
    public void run() throws Exception {

        // how many simulations to run per thread
        final var blockSize = this.sims / this.threads;

        // thread function
        final Callable<Map<Integer,BigInteger>> sim = () -> {

            final var cardValues = List.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11);
            final Random rand = new Random();

            // create result datastructures
            Map<Integer, BigInteger> threadResults = new HashMap<>();

            for (int j = 0; j < blockSize; j++) {
                // get sum of dice
                int sum = 0;
                for (int i = 0; i < 2; i++) sum += cardValues.get(rand.nextInt(1, cardValues.size()));

                var c = threadResults.getOrDefault(sum, BigInteger.ZERO);
                // increment counter
                c = c.add(BigInteger.ONE);
                // update value
                threadResults.put(sum, c);
            }

            // return results
            return threadResults;
        };

        // create new thread pool
        ExecutorService pool = Executors.newCachedThreadPool();

        final var startTime = Instant.now();

        // submit all jobs
        List<Future<Map<Integer, BigInteger>>> futures = new ArrayList<>();
        for (int i = 0; i < this.threads; i++) futures.add(pool.submit(sim));

        // wait for all threads to complete
        Map<Integer, BigInteger> finalResults = new HashMap<>();
        for (var future : futures) {
            var results = future.get();
            // tabulate results
            for (var key : results.keySet()) {
                var result = finalResults.getOrDefault(key, BigInteger.ZERO);
                finalResults.put(key, result.add(results.get(key)));
            }
        }

        final var endTime = Instant.now();

        Tools.shutdownPool(pool);

        final var duration = Duration.between(startTime, endTime);

        // print findings
        System.out.printf("Ran %d sims in %d minutes %d seconds", this.sims, duration.get(ChronoUnit.MINUTES), duration.get(ChronoUnit.SECONDS));
        for (Integer i : finalResults.keySet()) {
            BigDecimal resultBreakdown = new BigDecimal(finalResults.get(i));
            resultBreakdown = resultBreakdown.divide(BigDecimal.valueOf(this.sims), 5, RoundingMode.UP);
            resultBreakdown = resultBreakdown.multiply(BigDecimal.valueOf(100));
            if (i == 21)
                System.out.printf("Blackjack! %d hits, %.2f%% prob.\n", finalResults.get(i), resultBreakdown);
            else
                System.out.printf("Outcome: [%d] %d hits, %.2f%% prob.\n", i, finalResults.get(i), resultBreakdown);
        }
    }
}
