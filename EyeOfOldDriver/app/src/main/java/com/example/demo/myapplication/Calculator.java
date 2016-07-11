package com.example.demo.myapplication;

import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by HeWenjie on 2016/7/9.
 */
// 计算模块

public class Calculator {
    private Queue<String> rt = new LinkedList<String>();

    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private int op_order(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '(':
                return 0;
            default:
                return -1;
        }
    }

    private Queue<String> mid_to_post(String expression) {
        while (!rt.isEmpty()) {
            rt.poll();
        }
        String s = expression.substring(0, expression.length()-1);
        String item = "", aux = "";
        Stack<Character> op= new Stack<Character>();
        for (int i = 0; i < s.length(); ++i) {
            if (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.') {
                item += s.charAt(i);
                if (i + 1 < s.length()) {
                    if ((Character.isDigit(s.charAt(i+1))) || s.charAt(i+1) == '.') {
                        continue;
                    }
                }
                rt.offer(item);
                item = "";
            } else if (s.charAt(i) == ')') {
                while (op.peek() != '(') {
                    rt.offer(aux + op.peek());
                    op.pop();
                }
                op.pop();
            } else if (op.empty() || s.charAt(i) == '(' || op_order(s.charAt(i)) > op_order(op.peek())) {
                op.push(s.charAt(i));
            } else {
                while (op_order(op.peek()) >= op_order(s.charAt(i))) {
                    rt.offer(aux + op.peek());
                    op.pop();
                    if (op.empty()) {
                        break;
                    }
                }
                op.push(s.charAt(i));
            }
        }
        if (!item.equals("")){
            rt.offer(item);
            item = "";
        }
        while (!op.empty()) {
            rt.offer(aux + op.peek());
            op.pop();
        }
        return rt;
    }

    private String calculate(Queue<String> q) {
        double op1, op2;
        Stack<Double> s = new Stack<Double>();
        while (!q.isEmpty()) {
            if (isNumeric(q.peek())) {
                s.push(Double.parseDouble(q.peek()));
            } else {
                if(s.empty()) return null;
                op2 = s.peek();
                s.pop();
                if(s.empty()) return null;
                op1 = s.peek();
                s.pop();
                if (q.peek().equals("+")) {
                    s.push(op1+op2);
                } else if (q.peek().equals("-")) {
                    s.push(op1-op2);
                } else if (q.peek().equals("*")) {
                    s.push(op1*op2);
                } else if (q.peek().equals("/")) {
                    s.push(op1/op2);
                }
            }
            q.poll();
        }
        if (s.size() == 1) {
            return String.valueOf(s.peek());
        }

        // 算式不合法
        return null;
    }

    public String run(String expression) {
        return calculate(mid_to_post(expression));
    }

}
