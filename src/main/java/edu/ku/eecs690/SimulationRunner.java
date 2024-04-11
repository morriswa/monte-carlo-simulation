package edu.ku.eecs690;


import edu.ku.eecs690.simulation.BlackJackSimulation;
import edu.ku.eecs690.simulation.MonteCarloSimulation;
import edu.ku.eecs690.simulation.Simulation;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class SimulationRunner {

    public static void main(String[] args) throws Exception {

        System.out.print("""
            Menu:
                1) Monte Carlo Dice
                2) Blackjack
                3) Monte Carlo 2x 6 Dice Test Suite
                
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

            System.out.print("Time trials: ");
            final int timeTrials = Integer.parseInt(read.nextLine());

            read.close();

            System.out.println("Running...");

            long totalTime = 0L;
            for (int i = 0; i < timeTrials; i++) {
                System.out.printf("\n\n%s TRIAL %d %s\n", "=".repeat(20), i+1, "=".repeat(20));
                long simTime = sim.run(true);
                totalTime += simTime;
            }

            System.out.printf("\n\n%s RESULTS %s\n", "=".repeat(20), "=".repeat(20));
            System.out.printf("Average execution time %d ms\n", totalTime/timeTrials);
        } else if (selection==2) {
            System.out.print("Threads: ");
            final int threads = Integer.parseInt(read.nextLine());
            System.out.print("Events: ");
            final long trials = Long.parseLong(read.nextLine());

            sim = new BlackJackSimulation(trials, threads);

            System.out.print("Time trials: ");
            final int timeTrials = Integer.parseInt(read.nextLine());

            read.close();

            System.out.println("Running...");

            long totalTime = 0L;
            for (int i = 0; i < timeTrials; i++) {
                System.out.printf("\n\n%s TRIAL %d %s\n", "=".repeat(20), i+1, "=".repeat(20));
                long simTime = sim.run(true);
                totalTime += simTime;
            }

            System.out.printf("\n\n%s RESULTS %s\n", "=".repeat(20), "=".repeat(20));
            System.out.printf("Average execution time %d seconds\n", totalTime/timeTrials);
        } else if (selection==3) {
            System.out.print("Trials: ");
            final int numTrials = Integer.parseInt(read.nextLine());

            File file = new File("output.txt");

            if (!file.delete()) {
                System.err.println("Cannot delete existing file");
                System.exit(1);
                return;
            }

            FileWriter fw = new FileWriter(file);

            int[] trials = { 1, 2, 10, 20, 100, 200, 1_000, 2_000, 10_000, 20_000, 100_000, 200_000, 1_000_000 };
            int[] threads = { 1, 2, 4, 6, 8, 12, 16, 24, 32, 48, 64, 128, 256, 512, 1024 };

            Simulation currentSim;
            for (int trial : trials) {
                for (int thread : threads) {
                    currentSim = new MonteCarloSimulation(2, 1, 6, (trial*1_000L), thread);

                    long nanos = 0;
                    for (int timeTrials = 0; timeTrials < numTrials; timeTrials++) {
                        nanos += currentSim.run(false);
                    }

                    fw.write(String.format("Trials %dx1000\t\tThreads %d\t\tAvg. Exec. %d ns\n", trial, thread, nanos/numTrials));
                }

                fw.flush();
            }

            fw.close();

        } else {
            System.err.println("Invalid selection...");
        }
    }
}

