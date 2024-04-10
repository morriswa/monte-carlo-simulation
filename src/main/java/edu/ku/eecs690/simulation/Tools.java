package edu.ku.eecs690.simulation;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Tools {

    /**
     * @param draws number of times to roll a (high - low) + 1 sided dice
     * @return sum of 2 rolled dice
     */
    public static int draw(Integer draws, List<Integer> cardValue) {
        int sum = 0;
        for (int i = 0; i < draws; i++) sum += cardValue.get(getRandomNumber(0, cardValue.size() - 1));
        return sum;
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
