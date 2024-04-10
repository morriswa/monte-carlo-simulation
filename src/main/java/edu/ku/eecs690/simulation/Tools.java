package edu.ku.eecs690.simulation;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Tools {

    /**
     * @param draws number of times to roll a (high - low) + 1 sided dice
     * @return sum of 2 rolled dice
     */
    public static int draw(Integer draws, List<Integer> cardValue, Random rand) {
        int sum = 0;
        for (int i = 0; i < draws; i++) sum += cardValue.get(rand.nextInt(1, cardValue.size()));
        return sum;
    }

    public static void shutdownPool(ExecutorService pool) {
        try { // shutdown the thread pool
            pool.shutdownNow();
            if (!pool.awaitTermination(15, TimeUnit.SECONDS))
                throw new RuntimeException("Pool refused to shutdown after 15 seconds...");
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
    }
}
