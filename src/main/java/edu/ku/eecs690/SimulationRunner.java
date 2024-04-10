package edu.ku.eecs690;


import edu.ku.eecs690.simulation.BlackJackSimulation;
import edu.ku.eecs690.simulation.MonteCarloSimulation;
import edu.ku.eecs690.simulation.Simulation;

import java.util.Scanner;

public class SimulationRunner
{
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

            sim = new MonteCarloSimulation(dice, 1, sides, 20_000_000L, threads);
        } else if (selection==2) {
            sim = new BlackJackSimulation( 20_000_000L, 40);
        } else {
            sim = new BlackJackSimulation( 20_000_000L, 40);
        }

        read.close();

        System.out.println("Starting...");
        sim.run();
    }
}

