package edu.ku.eecs690;


import edu.ku.eecs690.simulation.BlackJackSimulation;
import edu.ku.eecs690.simulation.MonteCarloSimulation;
import edu.ku.eecs690.simulation.Simulation;

import java.util.Scanner;

public class SimulationRunner {

    public static void main(String[] args) throws Exception {

        System.out.print("""
            Menu:
                1) Monte Carlo Dice
                2) Blackjack
                
            Selection:\s""");

        Scanner read = new Scanner(System.in);

        int selection = Integer.parseInt(read.nextLine());

        final Simulation sim;
        if (selection==1) {
            System.out.print("Number of dice: ");
            final int dice = Integer.parseInt(read.nextLine());
            System.out.print("Sides: ");
            final int sides = Integer.parseInt(read.nextLine());
            System.out.print("Threads: ");
            final int threads = Integer.parseInt(read.nextLine());
            System.out.print("Events: ");
            final long trials = Long.parseLong(read.nextLine());

            sim = new MonteCarloSimulation(dice, 1, sides, trials, threads);
        } else if (selection==2) {
            System.out.print("Threads: ");
            final int threads = Integer.parseInt(read.nextLine());
            System.out.print("Events: ");
            final long trials = Long.parseLong(read.nextLine());

            sim = new BlackJackSimulation(trials, threads);
        } else {
            System.err.println("Invalid selection...");
            return;
        }

        System.out.print("Time trials: ");
        final int timeTrials = Integer.parseInt(read.nextLine());

        read.close();

        System.out.println("Running...");

        long totalTime = 0L;
        for (int i = 0; i < timeTrials; i++) {
            System.out.printf("\n\n%s TRIAL %d %s\n", "=".repeat(20), i+1, "=".repeat(20));
            long simTime = sim.run();
            totalTime += simTime;
        }

        System.out.printf("\n\n%s RESULTS %s\n", "=".repeat(20), "=".repeat(20));
        System.out.printf("Average execution time %d seconds\n", totalTime/timeTrials);
    }
}

