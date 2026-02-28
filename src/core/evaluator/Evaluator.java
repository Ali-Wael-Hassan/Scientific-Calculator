package core.evaluator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import core.token.TokenType;
import core.token.Token;

public class Evaluator {
    // 1 Parameter
    private static Map<TokenType,  DoubleUnaryOperator> unaryOps = new HashMap<>();
    // 2 Parameter
    private static Map<TokenType,  DoubleBinaryOperator> binaryOps = new HashMap<>();

    @FunctionalInterface
    interface TableOperation {
        double apply(List<Double> table);
    }

    private static Map<TokenType, TableOperation> statOps = new HashMap<>();

    static {
        unaryOps.put(TokenType.SIN, Math::sin);
        unaryOps.put(TokenType.COS, Math::cos);
        unaryOps.put(TokenType.TAN, a -> {
            double mod = Math.abs(a % Math.PI);
            if (Math.abs(mod - Math.PI / 2) < 1e-10) return Double.NaN;
            return Math.tan(a);
        });
        
        unaryOps.put(TokenType.ASIN, a -> (a < -1 || a > 1) ? Double.NaN : Math.asin(a));
        unaryOps.put(TokenType.ACOS, a -> (a < -1 || a > 1) ? Double.NaN : Math.acos(a));
        unaryOps.put(TokenType.ATAN, Math::atan);
        unaryOps.put(TokenType.ABS, Math::abs);
        
        unaryOps.put(TokenType.SINH, Math::sinh);
        unaryOps.put(TokenType.COSH, Math::cosh);
        unaryOps.put(TokenType.TANH, Math::tanh);
        
        unaryOps.put(TokenType.ASINH, a -> Math.log(a + Math.sqrt(a * a + 1)));
        unaryOps.put(TokenType.ACOSH, a -> (a < 1) ? Double.NaN : Math.log(a + Math.sqrt(a * a - 1)));
        unaryOps.put(TokenType.ATANH, a -> (a <= -1 || a >= 1) ? Double.NaN : 0.5 * Math.log((1 + a) / (1 - a)));
        
        unaryOps.put(TokenType.SQRT, a -> (a < 0) ? Double.NaN : Math.sqrt(a));
        unaryOps.put(TokenType.LOG, a -> (a <= 0) ? Double.NaN : Math.log10(a));
        unaryOps.put(TokenType.LN, a -> (a <= 0) ? Double.NaN : Math.log(a));
        unaryOps.put(TokenType.LOG2, a -> (a <= 0) ? Double.NaN : Math.log(a) / Math.log(2));
        
        unaryOps.put(TokenType.EXP, Math::exp);
        unaryOps.put(TokenType.TEN_POW, a -> Math.pow(10, a));
        unaryOps.put(TokenType.UNARY_MINUS, a -> -a);
        unaryOps.put(TokenType.POSTFIX, a -> (a < 0) ? Double.NaN : Evaluator.factorial(a));

        binaryOps.put(TokenType.PLUS, (a, b) -> a + b);
        binaryOps.put(TokenType.MINUS, (a, b) -> a - b);
        binaryOps.put(TokenType.MULTIPLY, (a, b) -> a * b);
        binaryOps.put(TokenType.DIVIDE, (a, b) -> (b == 0) ? Double.NaN : a / b);
        
        binaryOps.put(TokenType.POWER, (a, b) -> {
            if (a < 0 && b % 1 != 0) return Double.NaN;
            return Math.pow(a, b);
        });
        
        binaryOps.put(TokenType.MODULO, (a, b) -> (b == 0) ? Double.NaN : a % b); 

        statOps.put(TokenType.MEAN, Evaluator::Mean);
        statOps.put(TokenType.MEDIAN, Evaluator::Median);
        statOps.put(TokenType.MODE, Evaluator::Mode);
        statOps.put(TokenType.PVARIANCE, Evaluator::pVariance);
        statOps.put(TokenType.PSTD_DEV, Evaluator::pStandardDeviation);
        statOps.put(TokenType.SVARIANCE, Evaluator::sVariance);
        statOps.put(TokenType.SSTD_DEV, Evaluator::sStandardDeviation);
    }

    private static double factorial(double n) {
        double res = 1;
        for (int i = 2; i <= (int)n; i++) res *= i;
        return res;
    }

    public static double evaluate(List<Token> postfix, Map<String, Double> variables, List<Double> table) {
        List<Double> stack = new ArrayList<>();

        for (Token token : postfix) {
            TokenType type = token.getType();

            if (type == TokenType.NUMBER) {
                stack.add(Double.parseDouble(token.getValue()));
            }
            else if (type == TokenType.IDENTIFIER) {
                String varName = token.getValue().toLowerCase();
                stack.add(variables.getOrDefault(varName, 0.0));
            }
            else if (unaryOps.containsKey(type)) {
                double a = pop(stack);
                stack.add(unaryOps.get(type).applyAsDouble(a));
            } 
            else if (binaryOps.containsKey(type)) {
                double b = pop(stack); 
                double a = pop(stack);
                stack.add(binaryOps.get(type).applyAsDouble(a, b));
            }
            else if (statOps.containsKey(type)) {
                stack.add(statOps.get(type).apply(table));
            }
        }

        return stack.isEmpty() ? 0.0 : stack.get(0);
    }

    private static double Mean(List<Double> table) {
        if(table == null || table.size() < 1) return 0;

        double result = 0;
        
        for(double val : table) {
            result += val;
        }
        
        return result / table.size();
    }

    private static double Median(List<Double> table) {
        if(table == null || table.size() < 1) return 0;

        List<Double> sortedTable = new ArrayList<>(table);
        Collections.sort(sortedTable);
        int idx;
        if((table.size() & 1) == 1) {
            idx = (table.size() + 1) / 2 - 1;
            return table.get(idx);
        }

        idx = (sortedTable.size()) / 2 - 1;
        
        return (sortedTable.get(idx) + sortedTable.get(idx + 1)) / 2;
    }

    private static double Mode(List<Double> table) {
        if(table == null || table.size() < 1) return 0;

        List<Double> sortedTable = new ArrayList<>(table);
        Collections.sort(sortedTable);

        double mode = sortedTable.get(0);
        int maxCount = 0;

        int left = 0;
        while (left < sortedTable.size()) {
            int right = left;
            
            while (right < sortedTable.size() && sortedTable.get(right) == sortedTable.get(left)) {
                right++;
            }

            int currentCount = right - left;
            if (currentCount > maxCount) {
                maxCount = currentCount;
                mode = sortedTable.get(left);
            }

            left = right;
        }

        return mode;
    }

    private static double pVariance(List<Double> table) {
        if(table == null || table.size() < 1) return 0;

        double mean = Mean(table);
        double result = 0;

        for(double val : table) {
            result += (val - mean) * (val - mean);
        }
        
        return result / table.size();
    }

    private static double pStandardDeviation(List<Double> table) {  
        return Math.sqrt(pVariance(table));
    }

    private static double sVariance(List<Double> table) {
        if(table == null || table.size() < 2) return 0;

        double mean = Mean(table);
        double result = 0;

        for(double val : table) {
            result += (val - mean) * (val - mean);
        }
        
        return result / (table.size() - 1);
    }

    private static double sStandardDeviation(List<Double> table) {  
        return Math.sqrt(sVariance(table));
    }

    private static double pop(List<Double> stack) {
        if (stack.isEmpty()) return 0.0;
        return stack.remove(stack.size() - 1);
    }
}
