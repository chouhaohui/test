package com.example.demo.myapplication;

import java.util.InputMismatchException;

/**
 * Created by HuangYongjie on 2016/7/14.
 */
public class EquationCalcaulator {
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d*)?");  //match a number with optional '-' and decimal.
    }

    private double[] get_coe(String str) {
        double[] ans;
        ans = new double[3];
        String[] ss = str.split("\\+|-|\\*|/|\\(|\\)|=");
        for (String s:ss) {
            if (s.equals("")) {
                continue;
            }
            if (isNumeric(s)) {
                ans[2] = Double.parseDouble(s);
            } else if (s.indexOf("x") != -1) {
                if (s.indexOf("x") == 0) {
                    ans[0] = 1;
                } else {
                    ans[0] = Double.parseDouble(s.substring(0, s.length() - 1));
                }
                if (str.indexOf(s) > 0 && str.charAt(str.indexOf(s)-1) == '-') {
                    ans[0] = -ans[0];
                }
            } else {
                if (s.indexOf("y") == 0) {
                    ans[1] = 1;
                } else {
                    ans[1] = Double.parseDouble(s.substring(0, s.length() - 1));
                }
                if (str.indexOf(s) > 0 && str.charAt(str.indexOf(s)-1) == '-') {
                    ans[1] = -ans[1];
                }
            }
        }
        return ans;
    }

    // 只需要把包含方程的字符串数组传入即可得到一个解的数组，x = ans[0], y = ans[1]
    // 例如：
    //  String[] s----> s[0] = "4.5x+6y=2"     s[1] = "x+y=3".
    //  调用solution(s） 即可得到一个关于解的数组
    public double[] solution(String[] functions) {
        if(functions.length < 2) return null;
        double a, b, c, d, e, f;
        double[] t = get_coe(functions[0]);
        a = t[0];
        b = t[1];
        e = t[2];
        double[] tt = get_coe(functions[1]);
        c = tt[0];
        d = tt[1];
        f = tt[2];

        double temp = a * d - b * c;
        Double TEMP = temp;
        if (TEMP.equals(0.0)) {
            return null;
        }
        double[] ans;
        ans = new double[2];
        ans[0] = (d * e - b * f) / temp;
        ans[1] = (a * f - c * e) / temp;
        for (int i = 0; i < 2; ++i) {
            if (ans[i] == -0.0) {
                ans[i] = 0.0;
            }
        }
        return ans;
    }
}
