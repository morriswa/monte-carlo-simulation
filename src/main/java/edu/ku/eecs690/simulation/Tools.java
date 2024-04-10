package edu.ku.eecs690.simulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Tools {

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
