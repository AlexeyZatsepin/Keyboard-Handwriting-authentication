package com.security.keyboard.handwriting.math;

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
     * @return dispersion values list
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
//    public static List<Double> coefficientStudenta(Map<String,Long> pressingLength,List<Double> mathExpectation,List<Double> dispersion){
//        //cof studenta
//        int i=0;
//        ArrayList<Double> coefficient=new ArrayList<Double>();
//        for (String key:pressingLength.keySet()) {
//            coefficient.add(abs((pressingLength.get(key)-mathExpectation.get(i))/sqrt(dispersion.get(i))));
//            i++;
//        }
//        return coefficient;
//    }


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
        double Fp;
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
            authSize = standardSize;
        }else if(standardSize > authSize){
            List<Double> temp=new ArrayList<Double>(authSize);
            for (int i = 0; i < authSize; i++) {
                temp.add(SStandard.get(i));
            }
            SStandard=temp;
            standardSize=authSize;
        }
        double theoreticalF=getTheoreticalFisher(authSize);
        for (int i = 0; i < standardSize; i++) {
            double Smin=min(SStandard.get(i),SAuth.get(i));
            double Smax=max(SStandard.get(i),SAuth.get(i));
            Fp=Smax/Smin;
            if (Fp>theoreticalF){
                return false;
            }
        }
        return true;
    }
    /**
     *
     * @param value backspaces count in auth, and time (fullTime)
     * @param standard backspaces count in account data , and standart time
     * @return true if verified
     */
    public static boolean limitValueChecker(long value, long standard,int limit){
        long diff = value - standard;
        return !((diff > limit) && (diff > 0) || (diff < 0) && (diff < -limit));
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
        theorFisher.put(30,2.0423);
        theorFisher.put(40,2.0211);
        theorFisher.put(60,2.0003);
        if( n >= 60){
            return theorFisher.get(60);
        }else if(n>=40){
            return theorFisher.get(40);
        }else if(n>=30){
            return theorFisher.get(30);
        }else if(n>=20){
            return theorFisher.get(20);
        }else{
            return theorFisher.get(n);
        }
    }
    /***
     *
     * @param pressingLength model's length of pause between clicks (x)
     * @return Mx's values List, for My use ALPHA = 1
     */
    private static double M(Map<String, Long> pressingLength){
        double x = 0;
        for (String key : pressingLength.keySet()) {
            x += pressingLength.get(key);
        }
        return x/ pressingLength.size();
    }

    /**
     * @param mathExpectation math expectation for full list
     * @return dispersion on full array
     */
    private static double dispersionForFull(double mathExpectation, Map<String, Long> pressingLength){
        double sum = 0.0;
        for (String key : pressingLength.keySet()){
            double x = pressingLength.get(key) - mathExpectation;
            sum += x * x;
        }
        return sqrt(sum / (pressingLength.size() -1));
    }

    /***
     *
     * @param dispersionX Sx
     * @param dispersionY Sy
     * @return S for second method , square deviation
     */
    private static double meanSquareDeviation(double dispersionX, double dispersionY, int n){
        double S = ((pow(dispersionX,4) + pow(dispersionY,4)) * (n - 1));
        return Math.sqrt(S/(2*n-1));
    }

    private static double getTheoreticalStudent(int n){
        SparseArray<Double> theorStudent = new SparseArray<Double>();
        theorStudent.put(10,2.2281389);
        theorStudent.put(20,2.0859634);
        theorStudent.put(30,2.0422725);
        theorStudent.put(40,2.0210754);
        theorStudent.put(50,2.0085591);
        theorStudent.put(60,2.0002978);
        theorStudent.put(70,1.9944371);
        if(n >=70) return theorStudent.get(70);
        else if(n >= 60) return theorStudent.get(60);
        else if(n >= 50) return theorStudent.get(50);
        else if(n >= 40) return theorStudent.get(40);
        else if(n >= 30) return theorStudent.get(30);
        else if(n >= 20) return theorStudent.get(20);
        else if(n >= 10) return theorStudent.get(10);
        else             return theorStudent.get(10);
    }

    public static boolean studentCheck(Map<String,Long> Y,Map<String,Long> X){
        double My= M(X);
        double Mx= M(Y);
        double S = meanSquareDeviation(dispersionForFull(Mx,Y), dispersionForFull(My,X),X.size());
        return abs(Mx - My)/(S*(sqrt(2/X.size()))) > getTheoreticalStudent(X.size());
    }
}
