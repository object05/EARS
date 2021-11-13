package org.um.feri.ears.algorithms.tuning;

import org.um.feri.ears.algorithms.so.cro.CRO;

import java.util.ArrayList;


public class CRO_Tuning extends CRO {
    public CRO_Tuning(ArrayList<Double> conf, String aName) {
        super(conf.get(0).intValue(),conf.get(1).intValue(), conf.get(2), conf.get(3), conf.get(4), conf.get(5),conf.get(6).intValue()); //	public CRO(int n, int m, double rho, double fbs, double fa, double pd, int attemptsToSettle)
        aName = aName + "-n" + String.format("%.1f",conf.get(0))+ "-m" + String.format("%.1f",conf.get(1))+ "-rho" + String.format("%.1f",conf.get(2)) + "-fbs" + String.format("%.1f",conf.get(3)) + "-fa" + String.format("%.1f",conf.get(4)) + "-pd" + String.format("%.1f",conf.get(5)) + "-attemptsToSettle" + String.format("%.1f",conf.get(6));
        ai.setAcronym(aName);
        ai.setAcronym(aName);
        controlParameters = conf;
    }
}
