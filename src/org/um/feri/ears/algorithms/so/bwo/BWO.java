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

    private Task task;


    public BWO() {
        this(0.6, 0.44, 0.4, 50, 70);
    }

    public BWO(double pp, double cr, double pm, int npop, int maxiter) {
        super();
        this.pp = pp;
        this.cr = cr;
        this.pm = pm;
        this.npop = npop;
        this.maxiter = maxiter;
        setDebug(debug);  //EARS prints some debug info
        ai = new AlgorithmInfo("BWO", "Black widow optimisation", "");
        ai.addParameter(EnumAlgorithmParameters.UNNAMED1, pp + "");
        ai.addParameter(EnumAlgorithmParameters.CR, cr + "");
        ai.addParameter(EnumAlgorithmParameters.P_M, pm + "");
        ai.addParameter(EnumAlgorithmParameters.POP_SIZE, npop + "");
        ai.addParameter(EnumAlgorithmParameters.UNNAMED2, maxiter + "");
        au = new Author("Alan Hablak", "alan.hablak@student.um.si");
    }

    private ArrayList<DoubleSolution> DeepCopy(ArrayList<DoubleSolution> o, int to){
        if(to == -1) to = o.size();
        ArrayList<DoubleSolution> ret = new ArrayList<>();
        for(int i = 0; i < to; i++){
            ret.add(new DoubleSolution(o.get(i)));
        }
        return ret;
    }

    private ArrayList<DoubleSolution> generateNewPosition(Task t) throws StopCriterionException{
        ArrayList<DoubleSolution> res = new ArrayList<>();
        for(int i = 0; i < npop; i++){
            DoubleSolution temp = t.getRandomEvaluatedSolution();
            res.add(temp);
        }
        return res;
    }

    private ArrayList<DoubleSolution> generateNewPosition2(Task t) throws StopCriterionException{
        ArrayList<DoubleSolution> res = new ArrayList<>();
        int dof = t.getNumberOfDimensions();
        for(int i = 0; i < npop; i++){
            double[] temp = new double[dof];
            for(int j = 0; j < temp.length; j++){
                temp[j] = Util.nextDouble(-1,1);
            }
            //if(!task.isFeasible(temp)){
            //    temp = task.setFeasible(temp);
            //}
            res.add(t.eval(temp));
        }
        return res;
    }

    @Override
    public DoubleSolution execute(Task taskProblem) throws StopCriterionException {
        int dof;
        task = taskProblem;
        //task.incrementNumberOfIterations();

        //.enableEvaluationHistory();//todo
        dof = task.getNumberOfDimensions();

        int nr = (int)Math.floor(npop * pp);
        int nm = (int)Math.floor(npop * pm);
        ArrayList<DoubleSolution> pop = generateNewPosition2(task);

        for(int epoch = 0; epoch < maxiter; epoch++) {

            pop.sort(Comparator.comparing(DoubleSolution::getEval));
            ArrayList<DoubleSolution> pop1 = DeepCopy(pop, nr);
            ArrayList<DoubleSolution> pop2 = new ArrayList<DoubleSolution>();
            ArrayList<DoubleSolution> pop3 = new ArrayList<DoubleSolution>();

            gBest = pop.get(0);
            if (task.isStopCriterion()) {
                break;
            }
            for(int i = 0; i < nr; i++){
                if (task.isStopCriterion()) {
                    break;
                }
                int i1 = Util.nextInt(0, pop1.size());
                int i2 = Util.nextInt(0, pop1.size());
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
                }//todo delete weaker here
                if(task.isFirstBetter(p1,p2)){
                    //pop1.remove(i1);
                    pop1.remove(i2);
                }
                else{
                    //pop1.remove(i2);
                    pop1.remove(i1);
                }
                ArrayList<DoubleSolution> tempChildren = new ArrayList<DoubleSolution>();
                for (double[] l : children) {
                    tempChildren.add(task.eval(l));
                }
                tempChildren.sort(Comparator.comparing(DoubleSolution::getEval));
                tempChildren = DeepCopy(tempChildren, (int)(Math.max(children.size() * cr, 1)));
                for(int j = 0; j < tempChildren.size(); j++){
                    pop2.add(tempChildren.get(j));
                }
            }
            for(int i = 0; i < nm; i++){
                if (task.isStopCriterion()) {
                    break;
                }
                DoubleSolution m = pop2.get(Util.nextInt(0, pop2.size()));
                int cp1 = Util.nextInt(0, dof);
                int cp2 = Util.nextInt(0, dof);
                double[] vars = m.getDoubleVariables();
                double temp = vars[cp1];
                vars[cp1] = vars[cp2];
                vars[cp2] = temp;
                //List<Double> vars = m.getVariables();
                //double temp = vars.get(cp1);
                //vars.set(cp1, vars.get(cp2));
                //vars.set(cp2, temp);
                //m.setVariables(vars);//todo eval new
                pop3.add(task.eval(vars));
            }

            for(int i = 0; i < pop3.size(); i++){
                pop2.add(pop3.get(i));
            }
            pop = new ArrayList<>(pop2);
        }

        //double[] resValues = gBest.getDoubleVariables();
        //for(int i = 0; i < resValues.length; i++){
        //    resValues[i] = resValues[i] * task.getUpperLimit(i);
        //}
        //gBest = task.eval(resValues);

        return gBest;//todo multiply so we get big nums?
    }

    @Override
    public void resetToDefaultsBeforeNewRun() {

    }
}
