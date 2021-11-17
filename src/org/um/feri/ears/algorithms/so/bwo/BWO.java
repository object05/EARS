package org.um.feri.ears.algorithms.so.bwo;

import jdk.internal.vm.compiler.collections.Pair;
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
import java.util.List;

public class BWO extends Algorithm {

    @AlgorithmParameter(name = "procreating rate")
    private double pp;
    @AlgorithmParameter(name = "cannibalism rate")
    private double cr;
    @AlgorithmParameter(name = "mutation rate")
    private double pm;
    @AlgorithmParameter(name = "population size")
    private int npop;
    @AlgorithmParameter(name = "max iterations")
    private int maxiter;



    //@AlgorithmParameter(name = "dof")
    //private int dof;


    private ArrayList<Pair<Double,Double>> bounds;
    private ArrayList<Double> x0;

    private Task task;

    public BWO() {
        this(0.6, 0.44, 0.4, 10, 50, 2);
    }

    public BWO(double pp, double cr, double pm, int npop, int maxiter) {
        super();
        this.pp = pp;
        this.cr = cr;
        this.pm = pm;
        this.npop = npop;
        this.maxiter = maxiter;
        //this.dof = dof;
        setDebug(debug);  //EARS prints some debug info
        ai = new AlgorithmInfo("BWO", "Black widow optimisation", "");
        ai.addParameter(EnumAlgorithmParameters.UNNAMED1, pp + "");
        ai.addParameter(EnumAlgorithmParameters.CR, cr + "");
        ai.addParameter(EnumAlgorithmParameters.P_M, pm + "");
        ai.addParameter(EnumAlgorithmParameters.POP_SIZE, npop + "");
        ai.addParameter(EnumAlgorithmParameters.UNNAMED2, maxiter + "");
        ai.addParameter(EnumAlgorithmParameters.UNNAMED3, dof + "");
        au = new Author("Alan Hablak", "alan.hablak@student.um.si");
    }





    private ArrayList<Double> generateNewPosition(ArrayList<Double> _x0, int _dof, ArrayList<Pair<Double,Double>> _bounds){
        ArrayList<Double> resultPosition = new ArrayList<Double>();
        if(_x0 != null && _bounds != null){
            for(int i = 0; i < _x0.size(); i++){
                resultPosition.add(Math.min(Math.max(Util.nextDouble(-1,1)+_x0.get(i),_bounds.get(i).getLeft()), _bounds.get(i).getRight()));
            }
            return resultPosition;
        }
        else if(_bounds != null){
            for(int i = 0; i < _bounds.size(); i++){
                resultPosition.add(Util.nextDouble(_bounds.get(i).getLeft(), _bounds.get(i).getRight()));
            }
            return resultPosition;
        }
        else if(_x0 != null){
            for(int i = 0; i < _x0.size(); i++) {
                resultPosition.add(_x0.get(i) + Util.nextDouble(-1,1));
            }
            return resultPosition;
        }
        else if(_dof > 0){//todo?
            for(int i = 0; i < _dof; i++){
                resultPosition.add(Util.nextDouble(-1,1));
            }
            return resultPosition;
        }
        else {
            return null;
        }
    }

    @Override
    public DoubleSolution execute(Task taskProblem) throws StopCriterionException {
        int dof;

        task = taskProblem;
        dof = task.getNumberOfDimensions();

        //todo checks before
        if(bounds != null){
            for (Pair<Double,Double> bound : bounds) {
                if(bound.getLeft() > bound.getRight()){
                    //todo error
                    return null;
                }
            }
        }

        if(x0 != null) dof = x0.size();
        else if(bounds != null) dof = bounds.size();

        int nr = (int)Math.floor(npop * pp);
        int nm = (int)Math.floor(npop * pm);

        ArrayList<ArrayList<Double>> pop = new ArrayList<ArrayList<Double>>();
        for(int i = 0; i < npop; i++){
            pop.add(generateNewPosition(x0,dof,bounds));
        }

        while (!task.isStopCriterion()){//todo correct place?
            for(int epoch = 0; epoch < maxiter; epoch++){

            }
        }



        return null;
    }

    @Override
    public void resetToDefaultsBeforeNewRun() {

    }
}
