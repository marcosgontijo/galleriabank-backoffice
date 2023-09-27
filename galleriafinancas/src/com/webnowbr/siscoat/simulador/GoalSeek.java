package com.webnowbr.siscoat.simulador;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.webnowbr.siscoat.common.CommonsUtil;

public class GoalSeek {

    /**
     * The Decimal format.
     */
    private final DecimalFormat decimalFormat = new DecimalFormat("#0.000000");
    /**
     * The Target value.
     */
    private final double targetValue;

    /**
     * The Minimum boundary value.
     */
    private double minimumBoundaryValue;
    /**
     * The Maximum boundary value.
     */
    private double maximumBoundaryValue;

    /**
     * Instantiates a new Goal seek.
     *
     * @param targetValue          the target value
     * @param minimumBoundaryValue the minimum boundary value
     * @param maximumBoundaryValue the maximum boundary value
     */
    public GoalSeek(final double targetValue, final double minimumBoundaryValue, final double maximumBoundaryValue) {
        this.targetValue = targetValue;
        this.minimumBoundaryValue = minimumBoundaryValue;
        this.maximumBoundaryValue = maximumBoundaryValue;
    }

    /**
     * Gets mid value.
     *
     * @return the mid value
     */
    // bisection gives us the "average" of the point values
    public double getMidValue() {
        return (minimumBoundaryValue + maximumBoundaryValue) / 2;
    }

    /**
     * Check current value.
     *
     * @param currentValue the current value
     */
    public void checkCurrentValue(final double currentValue) {
        final double difference = getDifference(currentValue);
        final double midValue = getMidValue();
        if (difference < 0) {
            maximumBoundaryValue = midValue;
        } else {
            minimumBoundaryValue = midValue;
        }
    }

    /**
     * Gets difference.
     *
     * @param currentValue the current value
     * @return the difference
     */
    public double getDifference(final double currentValue) {
    	double temp = targetValue - currentValue;
        //return Double.parseDouble(decimalFormat.format(temp));
    	BigDecimal bigDecimal =  CommonsUtil.bigDecimalValue(temp).setScale(3, RoundingMode.DOWN);
    	temp = CommonsUtil.doubleValue(bigDecimal);
    	return temp;
    }

    /**
     * Gets minimum boundary value.
     *
     * @return the minimum boundary value
     */
    public double getMinimumBoundaryValue() {
        return minimumBoundaryValue;
    }

    /**
     * Gets maximum boundary value.
     *
     * @return the maximum boundary value
     */
    public double getMaximumBoundaryValue() {
        return maximumBoundaryValue;
    }

}
