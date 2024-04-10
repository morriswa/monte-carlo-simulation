package edu.ku.eecs690;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * required to run a monte-carlo simulation
 */
public class MonteCarloSimulation {

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

    MonteCarloSimulation(Integer rInputs, Integer lowOutput, Integer highOutput, Long sims, Integer threads) {
        this.rInputs = rInputs;
        this.lowOutput = lowOutput;
        this.highOutput = highOutput;
        this.sims = sims;
        this.threads = threads;
    }

    public void run() throws Exception {

        // how many simulations to run per thread
        final var blockSize = this.sims / this.threads;

        // thread function
        final Callable<Hashtable<Integer,AtomicInteger>> sim = () -> {

            // create result datastructures
            Hashtable<Integer, AtomicInteger> threadResults = new Hashtable<>();

            for (int j = 0; j < blockSize; j++) {
                // get sum of dice
                Integer sum = this.roll(this.rInputs, this.lowOutput, this.highOutput);
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
        List<Future<Hashtable<Integer, AtomicInteger>>> futures = new ArrayList<>();
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

        try { // shutdown the thread pool
            pool.shutdownNow();
            pool.awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }


        // print findings
        for (Integer i : finalResults.keySet()) {
            BigDecimal resultBreakdown = new BigDecimal(finalResults.get(i));
            resultBreakdown = resultBreakdown.divide(BigDecimal.valueOf(this.sims), 5, RoundingMode.UP);
            resultBreakdown = resultBreakdown.multiply(BigDecimal.valueOf(100));
            System.out.printf("%d: %d %.2f%%\n", i, finalResults.get(i), resultBreakdown);
        }
    }

    /**
     * @param rolls number of times to roll a (high - low) + 1 sided dice
     * @param low lowest val on die
     * @param high highest val on die
     * @return sum of 2 rolled dice
     */
    public static int roll(Integer rolls, Integer low, Integer high) {
        int sum = 0;
        for (int i = 0; i < rolls; i++) sum += getRandomNumber(low, high);
        return sum;
    }

    /**
     * @param min number that can be returned
     * @param max that can be returned
     * @return random integer (inclusive)
     */
    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * ((max+1) - min)) + min);
    }
}
