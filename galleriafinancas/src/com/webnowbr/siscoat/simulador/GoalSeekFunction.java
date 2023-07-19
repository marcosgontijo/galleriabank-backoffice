package com.webnowbr.siscoat.simulador;

import com.webnowbr.siscoat.simulador.GoalSeekFunction.ComputeInterface;

public class GoalSeekFunction {
//https://github.com/SP-2827/goal-seek-function/blob/main/GoalSeekFunction.java DEUS

    /**
     * The constant ITERATOR_LIMIT.
     */
    private static final int ITERATOR_LIMIT = 10000000;

    /**
     * The constant THRESHOLD.
     */
    private static final double THRESHOLD = 0.000_000_000_1;

    /**
     * The interface Compute interface.
     * use this to implement the required computation logic
     */
    public interface ComputeInterface {
        /**
         * Compute double.
         *
         * @param currentValue the current value
         * @return the double
         */
        double compute(double currentValue);
    }

    /**
     * Gets goal seek.
     *
     * @param goalSeek         the goal seek
     * @param computeInterface the compute interface
     * @return the goal seek
     */
    public Double getGoalSeek(final GoalSeek goalSeek, final ComputeInterface computeInterface) {
        int iterator = 1; /* how many times bisection has been performed */
        double currentValue;
        do {
        	double midvalue = goalSeek.getMidValue();
            currentValue = computeInterface.compute(midvalue); // passing as percent
            if (!Double.isInfinite(currentValue) && !Double.isNaN(currentValue)) {
                // evaluate function at midpoint & determine next interval bound
                goalSeek.checkCurrentValue(currentValue);
                if (goalSeek.getDifference(currentValue) == 0d) {
                	/*System.out.println("it:" + iterator);
                	System.out.println("VL:" + currentValue);
                	System.out.println("VB:" + midvalue);*/
                	return midvalue;
                }
            }
            /*System.out.println("it:" + iterator);
            System.out.println("VL:" + currentValue);
            System.out.println("VB:" + midvalue);*/
            iterator++; // increment iteration
        } while (Math.abs(goalSeek.getMinimumBoundaryValue() - goalSeek.getMaximumBoundaryValue()) / 2 >= THRESHOLD 
        		&& Math.abs(goalSeek.getDifference(currentValue)) > THRESHOLD && iterator <= ITERATOR_LIMIT);
        throw new RuntimeException("Goal not found");
        //return 0d;
    }

    /**
     * The entry point of application.
     *
     * @param arguments the input arguments
     */
    public static void main(String[] arguments) {
        //testGetGoalSeek()
        final GoalSeekFunction goalSeekFunction = new GoalSeekFunction();
        final Double goalSeek = goalSeekFunction.getGoalSeek(new GoalSeek(27000, 0, 100), a -> a * 100000);
        System.out.println(goalSeek);
    }
}
