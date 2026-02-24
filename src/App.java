import java.util.Scanner;
import java.util.List;
import java.util.Map;
import core.MathEngine;

public class App {
    public static final String RESET  = "\u001B[0m";
    public static final String RED    = "\u001B[31m";
    public static final String GREEN  = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE   = "\u001B[34m";
    public static final String CYAN   = "\u001B[36m";

    private static final String OK   = GREEN  + "[ OK ] " + RESET;
    private static final String ERR  = RED    + "[ ERR ] " + RESET;
    private static final String INFO = YELLOW + "[ INFO ] " + RESET;

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        
        MathEngine engine = new MathEngine();
        Scanner scanner = new Scanner(System.in);

        printHeader();

        while (true) {
            System.out.print(CYAN + "math-engine > " + RESET);
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) break;

            if (input.startsWith("add ")) {
                handlePush(engine, input.substring(4).trim());
            } 
            else if (input.equalsIgnoreCase("remove")) {
                engine.popFromTable();
                System.out.println(INFO + "Last entry removed from table.");
            } 
            else if (input.startsWith("set ")) {
                handleSet(engine, input);
            } 
            else if (input.equalsIgnoreCase("clear")) {
                engine.clearVariables();
                System.out.println(INFO + "All variables cleared.");
            } 
            else if (input.equalsIgnoreCase("show")) {
                printState(engine);
            }
            else {
                try {
                    double result = engine.calculate(input);
                    System.out.println(GREEN + "Result: " + RESET + result);
                } catch (Exception e) {
                    System.out.println(ERR + "Calculation Error: " + e.getMessage());
                }
            }
            System.out.println();
        }

        System.out.println(CYAN + "Goodbye! Happy calculating." + RESET);
        scanner.close();
    }

    private static void handlePush(MathEngine engine, String expr) {
        try {
            double val = engine.calculate(expr);
            engine.pushToTable(val);
            System.out.println(OK + "Added " + val + " to statistics table.");
        } catch (Exception e) {
            System.out.println(ERR + "Invalid Add Expression: " + e.getMessage());
        }
    }

    private static void handleSet(MathEngine engine, String input) {
        String[] parts = input.split("\\s+");
        if (parts.length >= 3) {
            String varName = parts[1];
            String expr = input.substring(input.indexOf(parts[2])).trim();
            try {
                double val = engine.calculate(expr);
                engine.setVariable(varName, val);
                System.out.println(OK + "Variable [" + YELLOW + varName + RESET + "] = " + val);
            } catch (Exception e) {
                System.out.println(ERR + "Assignment Error: " + e.getMessage());
            }
        } else {
            System.out.println(INFO + "Usage: set <variable_name> <expression>");
        }
    }

    private static void printState(MathEngine engine) {
        Map<String, Double> vars = engine.getVariables();
        List<Double> table = engine.getTable();

        System.out.println(BLUE + "\n================ REGISTER STATE ================" + RESET);
        
        System.out.printf(BLUE + "| " + RESET + "%-15s " + BLUE + "| " + RESET + "%-26s " + BLUE + "|%n" + RESET, "VARIABLE", "VALUE");
        System.out.println(BLUE + "|-----------------|----------------------------|" + RESET);
        
        if (vars.isEmpty()) {
            System.out.printf(BLUE + "| " + RESET + "%-43s " + BLUE + "|%n" + RESET, "No variables defined.");
        } else {
            vars.forEach((k, v) -> 
                System.out.printf(BLUE + "| " + RESET + "%-15s " + BLUE + "| " + RESET + YELLOW + "%-26.4f " + BLUE + "|%n" + RESET, k, v)
            );
        }
        
        System.out.println(BLUE + "|----------------------------------------------|" + RESET);
        System.out.println(BLUE + "| STATISTICS TABLE                             |" + RESET);
        System.out.println(BLUE + "|----------------------------------------------|" + RESET);
        
        if (table.isEmpty()) {
            System.out.printf(BLUE + "| " + RESET + "%-43s " + BLUE + "|%n" + RESET, "Table is empty.");
        } else {
            String listString = table.toString();
            if (listString.length() > 40) {
                System.out.printf(BLUE + "| " + RESET + "%-40s... " + BLUE + "|%n" + RESET, listString.substring(0, 40));
            } else {
                System.out.printf(BLUE + "| " + RESET + "%-43s " + BLUE + "|%n" + RESET, listString);
            }
        }
        System.out.println(BLUE + "================================================" + RESET + "\n");
    }

    private static void printHeader() {
        System.out.println(CYAN + "================================================");
        System.out.println("             JAVA MATH ENGINE v2.0              ");
        System.out.println("================================================");
        System.out.println(YELLOW + "  COMMANDS:" + RESET);
        System.out.println("   > set <var> <expr>  : Save a variable");
        System.out.println("   > add <expr>        : Add result to stats table");
        System.out.println("   > remove            : Remove last table entry");
        System.out.println("   > clear             : Reset all variables");
        System.out.println("   > show              : View variables & table");
        System.out.println("   > exit / quit       : Close program");
        System.out.println(YELLOW + "  MATH:" + RESET);
        System.out.println("   > Supports: +, -, *, /, ^, !, (), sin, cos,");
        System.out.println("               tan, log, sqrt, mean, median...");
        System.out.println(CYAN + "================================================" + RESET);
    }
}