package edu.ku.eecs690;


public class SimulationRunner
{
    public static void main(String[] args) throws Exception {
        final var sim = new MonteCarloSimulation(2,  1, 6, 200_000_000L, 4);
        sim.run();
    }
}

