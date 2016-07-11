import java.text.DecimalFormat;
import java.util.*;

public class Main {
    private static Queue<String> rt = new LinkedList<String>();

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private static boolean validateExpression(String expression) {
        if (expression.endsWith("=")) {
            expression = expression.substring(0, expression.length() - 1);
        }
        String[] ss = expression.split("\\+|-|\\*|/");
        for (String s:ss) {
            if (!isNumeric(s)) {
                return false;
            }
        }
        // 去掉算式中所有的合法项替换为"?"字符
        expression = expression.replaceAll(
                "\\d|\\.", "?");
        // 去掉替换后算式中所有的空格
        expression = expression.replaceAll(" ", "");
        // 如果有两个相邻的项中间没有操作符，则算式不合法
        if (expression.matches("^??$")) {
            return false;
        }
        // 必须是倒数第二步：判断小括号左右括弧是否等同，括弧位置是否合法,如果括弧全部合法，则去掉所有括弧
        int num = 0;
        char[] expChars = expression.toCharArray();
        for (int i = 0; i < expChars.length; i++) {
            char temp = expChars[i];
            if (temp == '(') {
                num++;
            } else if (temp == ')') {
                num--;
            }
            if (num < 0) {
                return false;
            }
        }
        if (num > 0) {
            return false;
        }
        expression = expression.replaceAll("\\(|\\)", "");
        // 必须是最后一步：判断仅剩的+-*/四则运算算式是否合法
        if (expression.matches("^\\?*((\\+|-|\\*|\\/)\\?*)*$")) {
            return true;
        } else {
            return false;
        }
    }

    private static int op_order(char op) {
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

    private static Queue<String> mid_to_post(String expression) {
        if (!expression.endsWith("=")) {
            expression += '=';
        }
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

    private static double calculate(Queue<String> q) throws InputMismatchException {
        double op1, op2;
        Stack<Double> s = new Stack<Double>();
        while (!q.isEmpty()) {
            if (isNumeric(q.peek())) {
                s.push(Double.parseDouble(q.peek()));
            } else {
                op2 = s.peek();
                s.pop();
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
            return s.peek();
        }
        throw new InputMismatchException("Error! Please enter another expression");
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Please input an expression such as (3+4)*5=\n");
        String expression;
        while (true) {
            expression = in.next();
            try {
                String style = "0.####";
                DecimalFormat df = new DecimalFormat();
                df.applyPattern(style);
                if (expression.startsWith("+") || expression.startsWith("-")) {
                    expression = "0" + expression;
                }
                if (validateExpression(expression)) {
                    double ans = calculate(mid_to_post(expression));
                    System.out.println("The answer is : " + df.format(ans));
                } else {
                    System.out.println("Invalid input!");
                }
            } catch (InputMismatchException s) {
                System.out.println("Oooops! Exception thrown : " + s);
            }
        }

    }
}
