package kpi.security.keyboard.handwriting.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

/**
 * KeyboardHandwriting
 * Created 5/5/16, with IntelliJ IDEA
 *  Utils static class for keyboard statistic calculations
 * @author Alex
 */
public final class Utils {

    /**
     * 
     * @param pressingLength length of pause between clicks
     * @return mathExpectation values list 
     */
    public static List<Double> mathExpectation(Map<String,Long> pressingLength) {
        List<Double> mathExpectation = new ArrayList<Double>();
        int N = pressingLength.size() - 1;
        //math Expectation
        double expectation = 0.0;

        for (String key : pressingLength.keySet()) {
            for (String k : pressingLength.keySet()) {
                if (!k.equals(key)) {
                    expectation += pressingLength.get(k);
                }
            }
            mathExpectation.add(expectation / N);
            expectation = 0.0;
        }
        return mathExpectation;
    }

    /**
     * 
     * @param pressingLength length of pause between clicks
     * @param mathExpectation mathExpectation values list 
     * @return dispertion values list
     */
    public static List<Double> dispersion(Map<String,Long> pressingLength,List<Double> mathExpectation) {
        int N = pressingLength.size() - 1;
        //dispersion
        int i = 0;
        ArrayList<Double> dispersion = new ArrayList<Double>();
        for (String key : pressingLength.keySet()) {
            double x;
            double s = 0.0;
            for (String k : pressingLength.keySet()) {
                if (!k.equals(key)) {
                    x = pressingLength.get(k) - mathExpectation.get(i);
                    s += x * x;
                }
            }
            dispersion.add(sqrt(s / (N - 1)));
            i++;
        }
        return dispersion;
    }

    /**
     * @return coefficients Student values list
     */
    public static List<Double> coefficientStudenta(Map<String,Long> pressingLength,List<Double> mathExpectation,List<Double> dispersion){
        //cof studenta
        int i=0;
        ArrayList<Double> coefficient=new ArrayList<Double>();
        for (String key:pressingLength.keySet()) {
            coefficient.add(abs((pressingLength.get(key)-mathExpectation.get(i))/dispersion.get(i)));
            i++;
        }
        return coefficient;
    }


    /**
     * @return clear map of pressing time values, discard wrong values
     */
    public static Map<String,Long> discardingOutliers(Map<String,Long> pressingLength, List<Double> dispersion, List<Double> mathExpectation){
        Map<String,Long> clear=new HashMap<String,Long>();
        int i=0;
        for (String key: pressingLength.keySet()) {
            if((pressingLength.get(key)>=(mathExpectation.get(i) - 3*sqrt(dispersion.get(i))))
                    &&(pressingLength.get(key)<=(mathExpectation.get(i) + 3*sqrt(dispersion.get(i++))))){
                clear.put(key,pressingLength.get(key));
            }
        }
        return clear;
    }

    /**
     * @param SStandard data from user account
     * @param SAuth data from recognition mode
     * @return true if sequence converge
     */
    public static boolean fisherCheck(List<Double> SStandard,List<Double> SAuth){
        Double Fp;
        List<Double> theoretical=getTheoreticalFisher();
        for (int i = 0; i < SStandard.size(); i++) {
            Double Smin=min(SStandard.get(i),SAuth.get(i));
            Double Smax=max(SStandard.get(i),SAuth.get(i));
            Fp=Smax/Smin;
            if (Fp<theoretical.get(SStandard.size()+1)){
                return false;
            }
        }
        return true;
    }

    /**
     * @return theoretical fishers table
     */
    private static List<Double> getTheoreticalFisher(){
        List<Double> theorFisher = new ArrayList<Double>();
        theorFisher.add(3.841);
        theorFisher.add(5.991);
        theorFisher.add(7.813);
        theorFisher.add(9.488);
        theorFisher.add(11.070);
        theorFisher.add(12.592);
        theorFisher.add(14.067);
        theorFisher.add(15.507);
        theorFisher.add(16.919);
        theorFisher.add(18.307);
        theorFisher.add(18.675);
        theorFisher.add(21.026);
        theorFisher.add(22.363);
        theorFisher.add(23.685);
        theorFisher.add(24.996);
        theorFisher.add(26.296);
        theorFisher.add(27.587);
        theorFisher.add(28.869);
        theorFisher.add(30.144);
        theorFisher.add(31.410);
        theorFisher.add(32.671);
        theorFisher.add(33.924);
        theorFisher.add(35.172);
        theorFisher.add(36.415);
        theorFisher.add(37.652);
        theorFisher.add(38.885);
        theorFisher.add(40.113);
        theorFisher.add(41.337);
        theorFisher.add(42.557);
        theorFisher.add(43.773);
        return theorFisher;
    }

}
