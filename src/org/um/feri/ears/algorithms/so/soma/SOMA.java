package org.um.feri.ears.algorithms.so.soma;

import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.AlgorithmInfo;
import org.um.feri.ears.algorithms.Author;
import org.um.feri.ears.algorithms.EnumAlgorithmParameters;
import org.um.feri.ears.problems.DoubleSolution;
import org.um.feri.ears.problems.StopCriterionException;
import org.um.feri.ears.problems.Task;
import org.um.feri.ears.util.Util;
import org.um.feri.ears.util.annotation.AlgorithmParameter;

import java.util.ArrayList;

public class SOMA extends Algorithm {

    public enum Strategy {ALL_TO_ALL, ALL_TO_ALL_ADAPTIVE, ALL_TO_ONE, ALL_TO_ONE_RANDOM}

    @AlgorithmParameter(name = "population size",
            min = "10")
    private int popSize;
    @AlgorithmParameter(name = "path length",
            description = "defines how far an individual stops behind the Leader",
            min = "1.1",
            max = "5")
    private double pathLength;
    @AlgorithmParameter(name = "step",
            description = "defines the granularity with what the search space is sampled",
            min = "0.11",
            max = "pathLength")
    private double step;
    @AlgorithmParameter(name = "perturbation",
            description = "determines whether an individual will travel directly towards the Leader, or not",
            min = "0",
            max = "1")
    private double prt;
    @AlgorithmParameter(name = "strategy")
    private Strategy strategy;

    private DoubleSolution best;
    private int leaderId; //index of the best solution
    private Task task;
    private DoubleSolution[] population;

    public SOMA() {
        this(Strategy.ALL_TO_ALL);
    }

    public SOMA(int popSize) {
        this(Strategy.ALL_TO_ALL, popSize);
    }

    public SOMA(Strategy strategy) {
        this(strategy, 50);
    }

    public SOMA(Strategy strategy, int popSize) {
        this(strategy, popSize, 0.3, 3.1, 0.3);
    }

    public SOMA(Strategy strategy, int popSize, double step, double pathLength, double prt) {
        super();
        this.strategy = strategy;
        this.popSize = popSize;
        this.step = step;
        this.pathLength = pathLength;
        this.prt = prt;

        // if step parameter is larger than pathLength it will cause an infinite loop
        assert (step <= pathLength);

        au = new Author("miha", "miha.ravber@um.si");
        ai = new AlgorithmInfo("SOMA_" + strategy.name(), "Self-Organizing Migrating Algorithm " + strategy.name(),
                "@article{zelinka2016soma,"
                        + "title={SOMA—self-organizing migrating algorithm},"
                        + "author={Zelinka, Ivan},"
                        + "booktitle={Self-Organizing Migrating Algorithm},"
                        + "pages={3--49},"
                        + "year={2016},"
                        + "publisher={Springer}}"
        );
        ai.addParameter(EnumAlgorithmParameters.POP_SIZE, popSize + "");
    }

    @Override
    public DoubleSolution execute(Task taskProblem) throws StopCriterionException {
        task = taskProblem;
        initPopulation();

        while (!task.isStopCriterion()) {

            switch (strategy) {
                case ALL_TO_ALL:
                    population = allToAll();
                    break;
                case ALL_TO_ONE:
                    population = allToOne();
                    break;
                case ALL_TO_ONE_RANDOM:
                    population = allToOneRandom();
                    break;
                case ALL_TO_ALL_ADAPTIVE:
                    population = allToOneAdaptive();
                    break;
            }
            updateBestSolution();
            task.incrementNumberOfIterations();
        }
        return best;
    }

    private DoubleSolution[] allToAll() throws StopCriterionException {
        DoubleSolution[] newPopulation = new DoubleSolution[popSize];
        for (int i = 0; i < popSize; i++) {
            ArrayList<DoubleSolution> solutions = new ArrayList<>();
            for (int j = 0; j < popSize; j++) {
                if (i != j) {
                    solutions.addAll(getSolutionsOnJumpingPositions(population[i], population[j]));
                }
            }
            newPopulation[i] = getBestSolution(solutions);
        }
        return newPopulation;
    }

    private DoubleSolution[] allToOne() throws StopCriterionException {
        DoubleSolution[] newPopulation = new DoubleSolution[popSize];
        updateBestSolution();
        for (int i = 0; i < popSize; i++) {
            if (i == leaderId) {
                newPopulation[i] = new DoubleSolution(best);
            } else {
                ArrayList<DoubleSolution> soluitions = getSolutionsOnJumpingPositions(population[i], best);
                newPopulation[i] = getBestSolution(soluitions);
            }
        }
        return newPopulation;
    }

    private DoubleSolution[] allToOneRandom() throws StopCriterionException {
        DoubleSolution[] newPopulation = new DoubleSolution[popSize];
        int leaderId = Util.nextInt(popSize);
        DoubleSolution leader = population[leaderId];
        for (int i = 0; i < popSize; i++) {
            if (i == leaderId) {
                newPopulation[i] = new DoubleSolution(leader);
            } else {
                ArrayList<DoubleSolution> solutions = getSolutionsOnJumpingPositions(population[i], leader);
                newPopulation[i] = getBestSolution(solutions);
            }
        }
        return newPopulation;
    }

    private DoubleSolution[] allToOneAdaptive() throws StopCriterionException {
        DoubleSolution[] newPopulation = new DoubleSolution[popSize];
        for (int i = 0; i < popSize; i++) {
            DoubleSolution jumpingSolution = population[i];
            for (int j = 0; j < popSize; j++) {
                if (i != j) {
                    ArrayList<DoubleSolution> solutions = getSolutionsOnJumpingPositions(jumpingSolution, population[j]);
                    jumpingSolution = getBestSolution(solutions);
                }
            }
            newPopulation[i] = jumpingSolution;
        }
        return newPopulation;
    }

    private void updateBestSolution() {
        for (int i = 0; i < popSize; i++) {
            if (task.isFirstBetter(population[i], best)) {
                best = new DoubleSolution(population[i]);
                leaderId = i;
            }
        }
    }

    private void initPopulation() throws StopCriterionException {
        population = new DoubleSolution[popSize];
        best = task.getRandomEvaluatedSolution();
        leaderId = 0;
        population[0] = new DoubleSolution(best);
        for (int i = 1; i < popSize; i++) {
            if (task.isStopCriterion())
                break;
            population[i] = task.getRandomEvaluatedSolution();
            if (task.isFirstBetter(population[i], best)) {
                best = new DoubleSolution(population[i]);
                leaderId = i;
            }
        }
    }

    private DoubleSolution getBestSolution(ArrayList<DoubleSolution> solutions) {
        DoubleSolution bestSolution = solutions.get(0);
        for (int i = 1; i < solutions.size(); i++) {
            if (solutions.get(i) == null)
                continue;
            if (task.isFirstBetter(solutions.get(i), bestSolution)) {
                bestSolution = solutions.get(i);
            }
        }
        return bestSolution;
    }

    private ArrayList<DoubleSolution> getSolutionsOnJumpingPositions(DoubleSolution jumpingSolution, DoubleSolution towardsSolution) throws StopCriterionException {
        ArrayList<DoubleSolution> solutions = new ArrayList<>();
        solutions.add(jumpingSolution);
        for (int i = 1; i * step <= pathLength; i++) {
            if (task.isStopCriterion())
                return solutions;
            DoubleSolution addSolution = getSolutionOnStep(jumpingSolution, towardsSolution, i * step);
            solutions.add(addSolution);
        }
        return solutions;
    }

    private DoubleSolution getSolutionOnStep(DoubleSolution jumpingSolution, DoubleSolution towardsSolution, double jump) throws StopCriterionException {
        double[] newSolution = new double[task.getNumberOfDimensions()];
        boolean[] prtVector = createPrtVector();
        for (int i = 0; i < task.getNumberOfDimensions(); i++) {
            if (prtVector[i]) {
                newSolution[i] = jumpingSolution.getValue(i) + (towardsSolution.getValue(i) - jumpingSolution.getValue(i)) * jump;
                while (newSolution[i] < task.getLowerLimit(i)) {
                    newSolution[i] += task.getUpperLimit(i) - task.getLowerLimit(i);
                }
                while (newSolution[i] > task.getUpperLimit(i)) {
                    newSolution[i] -= task.getUpperLimit(i) - task.getLowerLimit(i);
                }
            } else {
                newSolution[i] = jumpingSolution.getValue(i);
            }
        }
        return task.eval(newSolution);
    }

    private boolean[] createPrtVector() {
        boolean[] prtVector = new boolean[task.getNumberOfDimensions()];
        boolean ok = false;
        do {
            for (int i = 0; i < prtVector.length; i++) {
                prtVector[i] = Util.nextDouble() < prt;
                if (prtVector[i]) {
                    ok = true;
                }
            }
        } while (!ok);
        return prtVector;
    }

    @Override
    public void resetToDefaultsBeforeNewRun() {
    }
}