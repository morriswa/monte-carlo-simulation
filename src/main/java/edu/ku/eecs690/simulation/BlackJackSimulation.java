package edu.ku.eecs690.simulation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static edu.ku.eecs690.simulation.Tools.draw;

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
        final Callable<Map<Integer,AtomicInteger>> sim = () -> {

            // create result datastructures
            Map<Integer, AtomicInteger> threadResults = new HashMap<>();

            for (int j = 0; j < blockSize; j++) {
                // get sum of dice
                Integer sum = draw(2, List.of(2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10, 11));
                var c = threadResults.getOrDefault(sum, new AtomicInteger(0));
                // increment counter
                c.getAndIncrement();
                // update value
                threadResults.put(sum, c);
            }

            // return results
            return threadResults;
        };

        // create new thread pool
        ExecutorService pool = Executors.newCachedThreadPool();

        // submit all jobs
        List<Future<Map<Integer, AtomicInteger>>> futures = new ArrayList<>();
        for (int i = 0; i < this.threads; i++) futures.add(pool.submit(sim));

        // wait for all threads to complete
        Hashtable<Integer, BigInteger> finalResults = new Hashtable<>();
        for (var future : futures) {
            var results = future.get();
            for (var key : results.keySet()) {
                var result = finalResults.getOrDefault(key, BigInteger.ZERO);
                finalResults.put(key, result.add(BigInteger.valueOf(results.get(key).get())));
            }
        }

        Tools.shutdownPool(pool);

        // print findings
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
