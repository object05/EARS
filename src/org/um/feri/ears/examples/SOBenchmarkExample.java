package org.um.feri.ears.examples;

import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.so.abc.ABC;
import org.um.feri.ears.algorithms.so.bro.BRO;
import org.um.feri.ears.algorithms.so.bwo.BWO;
import org.um.feri.ears.algorithms.so.cro.CRO;
import org.um.feri.ears.algorithms.so.gwo.GWO;
import org.um.feri.ears.algorithms.so.jade.JADE;
import org.um.feri.ears.algorithms.so.random.RandomWalkAlgorithm;
import org.um.feri.ears.algorithms.so.tlbo.TLBOAlgorithm;
import org.um.feri.ears.benchmark.Benchmark;
import org.um.feri.ears.benchmark.RPUOed30Benchmark;
import org.um.feri.ears.util.Util;

import java.util.ArrayList;

public class SOBenchmarkExample {

    public static void main(String[] args) {
        Util.rnd.setSeed(System.currentTimeMillis()); //set the seed of the random generator
        Benchmark.printInfo = false; //prints one on one results
        //add algorithms to a list
        ArrayList<Algorithm> algorithms = new ArrayList<Algorithm>();
        algorithms.add(new ABC());
        algorithms.add(new GWO());
        algorithms.add(new BWO());
        algorithms.add(new TLBOAlgorithm());
        algorithms.add(new RandomWalkAlgorithm());
        algorithms.add(new BRO());
        algorithms.add(new JADE());
        algorithms.add(new CRO());

        RPUOed30Benchmark rpuoed30 = new RPUOed30Benchmark(); // benchmark with prepared tasks and settings

        rpuoed30.addAlgorithms(algorithms);  // register the algorithms in the benchmark

        rpuoed30.run(10); //start the tournament with 10 runs/repetitions
    }
}
