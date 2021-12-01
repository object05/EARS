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
import java.util.Comparator;
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

    private DoubleSolution gBest;

    //@AlgorithmParameter(name = "dof")
    //private int dof;


    private ArrayList<Pair<Double,Double>> bounds;

    private ArrayList<Double> x0;

    private Task task;


    public BWO() {
        this(0.6, 0.44, 0.4, 10, 50);
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

    //TODO CHECK IF STOP CRITERION
    @Override
    public DoubleSolution execute(Task taskProblem) throws StopCriterionException {
        int dof;

        task = taskProblem;
        task.enableEvaluationHistory();
        dof = task.getNumberOfDimensions();

        if(x0 != null) dof = x0.size();
        else if(bounds != null) dof = bounds.size();

        int nr = (int)Math.floor(npop * pp);
        int nm = (int)Math.floor(npop * pm);

        ArrayList<ArrayList<Double>> popInit = new ArrayList<ArrayList<Double>>();
        for(int i = 0; i < npop; i++){
            popInit.add(generateNewPosition(x0,dof,bounds));
        }

        //ok

        ArrayList<DoubleSolution> pop = new ArrayList<DoubleSolution>();
        for(int epoch = 0; epoch < maxiter; epoch++) {
            for (ArrayList<Double> l : popInit) {
                double[] arr = l.stream().mapToDouble(Double::doubleValue).toArray();
                pop.add(task.eval(arr));
            }
            pop.sort(Comparator.comparing(DoubleSolution::getEval));//todo problematic?
            ArrayList<DoubleSolution> pop1 = (ArrayList<DoubleSolution>) pop.clone();//todo deepcopy ok?
            //ok
            ArrayList<DoubleSolution> pop2 = new ArrayList<DoubleSolution>();
            ArrayList<DoubleSolution> pop3 = new ArrayList<DoubleSolution>();

            gBest = pop.get(0);
            //ok
            for(int i = 0; i < nr; i++){
                int i1 = Util.nextInt(0, pop1.size()-1);
                int i2 = Util.nextInt(0, pop1.size()-1);
                DoubleSolution p1 = pop1.get(i1);
                DoubleSolution p2 = pop1.get(i2);
                ArrayList<double[]> children = new ArrayList<>();
                for(int j = 0; j < (int)(dof/2); j++){
                    double alpha = Util.nextDouble();
                    double[] c1 = new double[dof];
                    double[] c2 = new double[dof];
                    for(int k = 0; k < dof; k++){
                        c1[k] = (alpha * p1.getValue(k)) + ((1 - alpha) * p2.getValue(k));
                        c2[k] = (alpha * p2.getValue(k)) + ((1 - alpha) * p1.getValue(k));
                    }
                    children.add(c1);
                    children.add(c2);
                }
                if(task.eval(p1.getDoubleVariables()).getEval() < task.eval(p2.getDoubleVariables()).getEval()){
                    pop1.remove(i1);
                }
                else{
                    pop1.remove(i2);
                }
                //ok

                ArrayList<DoubleSolution> tempChildren = new ArrayList<DoubleSolution>();
                for (double[] l : children) {
                    tempChildren.add(task.eval(l));
                }
                tempChildren.sort(Comparator.comparing(DoubleSolution::getEval));//todo problematic?
                //ok
                tempChildren = new ArrayList<>(tempChildren.subList(0, (int)(Math.max(children.size() * cr, 1))));//todo unnecessary and working??
                for(int j = 0; j < tempChildren.size(); j++){
                    pop2.add(tempChildren.get(j));
                }
            }
            for(int i = 0; i < nm; i++){
                DoubleSolution m = pop2.get(Util.nextInt(0, pop2.size()-1));
                int cp1 = Util.nextInt(0, dof-1);
                int cp2 = Util.nextInt(0, dof-1);
                List<Double> vars = m.getVariables();
                double temp = vars.get(cp1);
                vars.set(cp1, vars.get(cp2));
                vars.set(cp2, temp);
                m.setVariables(vars);
                pop3.add(m);
            }

            for(int i = 0; i < pop3.size(); i++){
                pop2.add(pop3.get(i));
            }

            pop = (ArrayList<DoubleSolution>) pop2.clone();//todo deepcopy??
        }
        //TODO PYTHON RAND IS INCLUSIVE
        //IS IT ALSO IN HERE???
        //TODO isstopcriterion

        return task.eval(gBest.getDoubleVariables());//todo already evaled?
    }

    @Override
    public void resetToDefaultsBeforeNewRun() {

    }
}
