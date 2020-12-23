package org.um.feri.ears.problems.unconstrained;

import org.um.feri.ears.problems.Problem;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.pow;

/*
http://infinity77.net/global_optimization/test_functions_nd_B.html#go_benchmark.Brown
https://www.al-roomi.org/benchmarks/unconstrained/n-dimensions/241-brown-s-function
http://benchmarkfcns.xyz/benchmarkfcns/brownfcn.html
 */
public class Brown extends Problem {

    public Brown() {
        super(2, 0);
        lowerLimit = new ArrayList<Double>(Collections.nCopies(numberOfDimensions, -1.0));
        upperLimit = new ArrayList<Double>(Collections.nCopies(numberOfDimensions, 4.0));
        name = "Brown";
    }

    @Override
    public double eval(double[] x) {
        double fitness = 0;
        for (int i = 0; i < numberOfDimensions - 1; i++) {

            fitness += pow((pow(x[i], 2)), (pow(x[i + 1], 2) + 1)) + pow((pow(x[i + 1], 2)), (pow(x[i], 2) + 1));
        }
        return fitness;
    }
}