package core;

import core.lexer.Lexer;
import core.parser.Parser;
import core.evaluator.Evaluator;
import core.token.Token;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MathEngine {
    private final Map<String, Double> variables = new HashMap<>();
    private final List<Double> table = new ArrayList<>();

    public double calculate(String expression) {
        // Step 1: Lexing
        Lexer lexer = new Lexer(expression);
        List<Token> tokens = lexer.tokenize();

        // Step 2: Parsing
        Parser parser = new Parser(tokens);
        List<Token> postfix = parser.infixToPostfix();

        // Step 3: Evaluating
        return Evaluator.evaluate(postfix, variables, table);
    }

    public void setVariable(String name, double value) {
        variables.put(name, value);
    }

    public Map<String, Double> getVariables() {
        return variables;
    }
    
    public void clearVariables() {
        variables.clear();
    }

    public void pushToTable(double value) {
        table.add(value);
    }

    public void popFromTable() {
        table.removeLast();
    }

    public List<Double> getTable() {
        return table;
    }

}