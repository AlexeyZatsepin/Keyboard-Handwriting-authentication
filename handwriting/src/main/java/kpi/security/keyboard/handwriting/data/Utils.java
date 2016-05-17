package kpi.security.keyboard.handwriting.data;

import android.util.Log;
import android.util.SparseArray;

import java.util.*;

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
        int standardSize = SStandard.size();
        int authSize = SAuth.size();
        /**
         * use temporary list link to arraylist object
         */
        if (standardSize < authSize){
            List<Double> temp=new ArrayList<Double>(standardSize);
            for (int i = 0; i < standardSize; i++) {
                temp.add(SAuth.get(i));
            }
            SAuth=temp;
        }else if(standardSize > authSize){
            List<Double> temp=new ArrayList<Double>(authSize);
            for (int i = 0; i < authSize; i++) {
                temp.add(SStandard.get(i));
            }
            SStandard=temp;
        }
        Log.v("USERLIST Standart",SStandard.toString());
        Log.v("USERLIST Auth",SAuth.toString());

        Double theoretical=getTheoreticalFisher(authSize);
        for (int i = 0; i < standardSize; i++) {
            Double Smin=min(SStandard.get(i),SAuth.get(i));
            Double Smax=max(SStandard.get(i),SAuth.get(i));
            Fp=Smax/Smin;
            Log.d("USERLIST", String.valueOf(Fp));
            if (Fp<theoretical){
                return false;
            }
        }
        return true;
    }

    /**
     * @return theoretical fishers table
     * table realized by SparseArray, in connection with better performance than HashMap
     */
    private static Double getTheoreticalFisher(int n){
        SparseArray<Double> theorFisher = new SparseArray<Double>();
        theorFisher.put(1,12.706);
        theorFisher.put(2,4.3027);
        theorFisher.put(3,3.1825);
        theorFisher.put(4,2.7764);
        theorFisher.put(5,2.5706);
        theorFisher.put(6,2.4469);
        theorFisher.put(7,2.3646);
        theorFisher.put(8,2.3060);
        theorFisher.put(9,2.2622);
        theorFisher.put(10,2.2281);
        theorFisher.put(11,2.2010);
        theorFisher.put(12,2.1788);
        theorFisher.put(13,2.1604);
        theorFisher.put(14,2.1448);
        theorFisher.put(15,2.1315);
        theorFisher.put(16,2.1199);
        theorFisher.put(17,2.1098);
        theorFisher.put(18,2.1009);
        theorFisher.put(19,2.0930);
        theorFisher.put(20,2.0860);
        theorFisher.put(21,2.0796);
        theorFisher.put(22,2.0739);
        theorFisher.put(23,2.0687);
        theorFisher.put(24,2.0639);
        theorFisher.put(25,2.0595);
        theorFisher.put(26,2.0555);
        theorFisher.put(27,2.0518);
        theorFisher.put(28,2.0484);
        theorFisher.put(29,2.0452);
        theorFisher.put(30,2.0423);
//40
        theorFisher.put(40,2.0211);
//60
        theorFisher.put(60,2.0003);
        if( n >= 60){
            return theorFisher.get(60);
        }else if(n>=40){
            return theorFisher.get(40);
        }else if(n>=30){
            return theorFisher.get(30);
        }else {
            return theorFisher.get(n);
        }
    }

}

/*
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
        */
