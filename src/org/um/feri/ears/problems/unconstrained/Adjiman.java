package org.um.feri.ears.problems.unconstrained;

import org.um.feri.ears.problems.Problem;

import java.util.ArrayList;
import java.util.Collections;

/*
http://infinity77.net/global_optimization/test_functions_nd_A.html#go_benchmark.Adjiman
https://www.al-roomi.org/benchmarks/unconstrained/2-dimensions/113-adjiman-s-function
 */
public class Adjiman extends Problem {

    public Adjiman() {
        super(2, 0);
        lowerLimit = new ArrayList<Double>(Collections.nCopies(numberOfDimensions, 0.0));
        upperLimit = new ArrayList<Double>(Collections.nCopies(numberOfDimensions, 0.0));
        name = "Adjiman";

        lowerLimit.set(0, -1.0);
        upperLimit.set(0, 2.0);

        lowerLimit.set(1, -1.0);
        upperLimit.set(1, 1.0);

        optimum[0] = new double[]{2, 0.10578};
    }

    @Override
    public double eval(double[] x) {
        return Math.cos(x[0]) * Math.sin(x[1]) - x[0] / (1 + Math.pow(x[1], 2));
    }

    @Override
    public double getGlobalOptimum() {
        return -2.0218067833370204;
    }
}
