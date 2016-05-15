package kpi.security.keyboard.handwriting.data;


import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * KeyboardHandwriting
 * Created 5/5/16, with IntelliJ IDEA
 *
 * @author Alex
 */
public final class Utils {

    public static ArrayList<Double> mathExpectation(HashMap<String,Long> persistanceLenght) {
        ArrayList<Double> mathExpectation = new ArrayList<Double>();
        int N = persistanceLenght.size() - 1;
        //math Expectation
        double expectation = 0.0;

        for (String key : persistanceLenght.keySet()) {
            for (String k : persistanceLenght.keySet()) {
                if (!k.equals(key)) {
                    expectation += persistanceLenght.get(k);
                }
            }
            mathExpectation.add(expectation / N);
            expectation = 0.0;
        }
        return mathExpectation;
    }


    public static ArrayList<Double> dispertion(HashMap<String,Long> persistanceLenght,ArrayList<Double> mathExpectation) {
        int N = persistanceLenght.size() - 1;
        //dispersion
        int i = 0;
        ArrayList<Double> dispersion = new ArrayList<Double>();
        for (String key : persistanceLenght.keySet()) {
            double x;
            double s = 0.0;
            for (String k : persistanceLenght.keySet()) {
                if (!k.equals(key)) {
                    x = persistanceLenght.get(k) - mathExpectation.get(i);
                    s += x * x;
                }
            }
            dispersion.add(sqrt(s / (N - 1)));
            i++;
        }
        return dispersion;
    }
    public static ArrayList<Double> coefficientStudenta(HashMap<String,Long> persistanceLenght,ArrayList<Double> mathExpectation,ArrayList<Double> dispersion){
        //cof studenta
        int i=0;
        ArrayList<Double> coefficient=new ArrayList<Double>();
        for (String key:persistanceLenght.keySet()) {
            coefficient.add(abs((persistanceLenght.get(key)-mathExpectation.get(i))/dispersion.get(i)));
            i++;
        }
        return coefficient;
    }



}
