package edu.ku.eecs690.simulation;

public interface Simulation {

    /**
     * @return runtime in nanos
     * @throws Exception if an error occurs
     */
    long run(boolean enableLogging) throws Exception;
}
