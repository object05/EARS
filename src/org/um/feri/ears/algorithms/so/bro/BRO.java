package org.um.feri.ears.algorithms.so.bro;

import org.um.feri.ears.algorithms.Algorithm;
import org.um.feri.ears.algorithms.AlgorithmInfo;
import org.um.feri.ears.algorithms.Author;
import org.um.feri.ears.algorithms.EnumAlgorithmParameters;
import org.um.feri.ears.problems.DoubleSolution;
import org.um.feri.ears.problems.StopCriterionException;
import org.um.feri.ears.problems.Task;
import org.um.feri.ears.util.EuclideanDistance;
import org.um.feri.ears.util.Util;
import org.um.feri.ears.util.annotation.AlgorithmParameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BRO extends Algorithm {

    @AlgorithmParameter(name = "population size")
    private final int popSize;
    @AlgorithmParameter(name = "threshold")
    private double threshold;

    private Task task;

    private List<Player> players;

    public BRO(){this(100,3);}

    public BRO(int popSize, double threshold){
        this.popSize = popSize;
        this.threshold = threshold;

        au = new Author("leon", "leon.kutos@student.um.si");
        ai = new AlgorithmInfo("BRO", "Battle Royal Optimization",
                "@article{farshi2021royal,"
                        + "title={Battle royal optimization algorithm}, "
                        + "author={Taymaz Rahkar Farshi}, "
                        + "journal={Neural Computing and Applications}, volume={33},year={2021}}"
        );
        ai.addParameter(EnumAlgorithmParameters.POP_SIZE, popSize + "");
    }

    @Override
    public DoubleSolution execute(Task problem) throws StopCriterionException {
        task = problem;
        this.threshold = threshold * task.getNumberOfDimensions();
        createInitialPopulation();
        int dimension = task.getNumberOfDimensions();
        Player dam, vic, best;
        List<Double> damPos,bestPos;

        while(!task.isStopCriterion()){
            task.incrementNumberOfIterations();
            for (Player i : players) {
                Player j = findNearestPlayer(i);
                dam = i;
                vic = j;
                if(task.isFirstBetter(i,j)){
                 dam = j;
                 vic = i;
                }
                best = findBestInGeneration();
                damPos = dam.getVariables();
                bestPos = best.getVariables();
                if(dam.getDamage() < threshold){
                    for (int d = 0; d < dimension;d++){
                        damPos.set(d,Util.nextDouble(0,Math.max(damPos.get(d),bestPos.get(d))-Math.min(damPos.get(d),bestPos.get(d)))+Math.min(damPos.get(d),bestPos.get(d)));
                    }
                    dam.increaseDamage();
                    vic.resetDamage();
                    if(!task.isStopCriterion()){
                        task.eval(dam);
                    }
                } else {
                    for (int d = 0; d < dimension;d++){
                        damPos.set(d,Util.nextDouble(0,(task.getUpperLimit(d) - task.getLowerLimit(d)))+ task.getLowerLimit(d));//task.getLowerLimit(d),task.getUpperLimit(d)));
                    }
                    if(!task.isStopCriterion()){
                        task.eval(dam);
                        dam.resetDamage();
                    }
                }
            }
        }
        return findBestInGeneration();
    }

    private void createInitialPopulation() throws StopCriterionException {
        players = new ArrayList<>();
        for (int i = 0; i < popSize; i++) {
            Player p = new Player(task.getRandomEvaluatedSolution(), task.getNumberOfDimensions());
            players.add(p);
        }
    }

    private Player findNearestPlayer(Player a) {
        EuclideanDistance Distance = new EuclideanDistance();
        Distance.compute(a.getDoubleVariables(),a.getDoubleVariables());
        double minDistance = Double.MAX_VALUE;
        Player nearest = null;
        double d;
        for (Player player:players) {
            if(a == player)
                continue;
            d = Distance.compute(a.getDoubleVariables(),player.getDoubleVariables());
            if (d < minDistance){
                nearest = player;
                minDistance = d;
            }
        }
        return nearest;
    }

    private Player findBestInGeneration(){
        Player best = players.get(0);
        for(Player player : players){
            if(task.isFirstBetter(player,best))
                best = player;
        }
        return best;
    }

    @Override
    public void resetToDefaultsBeforeNewRun() {

    }

    static class Player extends DoubleSolution{
        private final int[] damage;

        Player (DoubleSolution solution, int dim){
            super(solution);
            damage = new int[dim];
        }

        public int getDamage(){
            int dmg = 0;
            for (int d:damage){
                dmg += d;
            }
            return dmg;
        }

        public void increaseDamage() {
            for (int index = 0; index < damage.length;index++){
                damage[index]++;
            }
        }

        public void resetDamage(){
            Arrays.fill(damage, 0);
        }
    }

}