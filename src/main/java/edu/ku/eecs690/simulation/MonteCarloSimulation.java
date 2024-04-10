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
public class MonteCarloSimulation implements Simulation {

    /**
     * random inputs
     */
    private final Integer rInputs;

    /**
     * lowest output of r-input
     */
    private final Integer lowOutput;

    /**
     * largest output of r-input
     */
    private final Integer highOutput;

    /**
     * total simulation to run
     */
    private final Long sims;

    /**
     * to run on
     */
    private final Integer threads;

    public MonteCarloSimulation(Integer rInputs, Integer lowOutput, Integer highOutput, Long sims, Integer threads) {
        this.rInputs = rInputs;
        this.lowOutput = lowOutput;
        this.highOutput = highOutput;
        this.sims = sims;
        this.threads = threads;
    }

    @Override
    public void run() throws Exception {

        // how many simulations to run per thread
        final var blockSize = this.sims / this.threads;

        System.out.printf("running %d threads, %d sims each\n", this.threads, blockSize);

        // thread function
        final Callable<Map<Integer,BigInteger>> sim = () -> {

            final Random rand = new Random();

            // create result datastructures
            Map<Integer, BigInteger> threadResults = new HashMap<>();

            for (long j = 0; j < blockSize; j++) {
                // get sum of dice
                Integer sum =
                        rand.nextInt(1, this.highOutput + 1)
                    +   rand.nextInt(1, this.highOutput + 1);
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
            for (var key : results.keySet()) {
                var result = finalResults.getOrDefault(key, BigInteger.ZERO);
                finalResults.put(key, result.add(results.get(key)));
            }
        }

        final var endTime = Instant.now();

        Tools.shutdownPool(pool);

        final var duration = Duration.between(startTime, endTime).get(ChronoUnit.SECONDS);

        // print findings
        System.out.printf("\nRan %d sims in %d minute(s) %d second(s)\n", this.sims, duration / 60, duration % 60);
        for (Integer i : finalResults.keySet()) {
            BigDecimal resultBreakdown = new BigDecimal(finalResults.get(i));
            resultBreakdown = resultBreakdown.divide(BigDecimal.valueOf(this.sims), 5, RoundingMode.UP);
            resultBreakdown = resultBreakdown.multiply(BigDecimal.valueOf(100));
            System.out.printf("Outcome: [%d] %d hits, %.2f%% prob.\n", i, finalResults.get(i), resultBreakdown);
        }
    }

}
