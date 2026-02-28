package gui.elements;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CalcKeys extends JPanel {
    
    private final Color bg_color = Color.decode("#0B1619");
    private final Color panel_color = Color.decode("#111F23");
    private final Color num_bg_color = Color.decode("#1E2A2E");
    private final Color accent_color = Color.decode("#00F5FF");
    private final Color danger_color = Color.decode("#FF2D55");
    private final Color text_primary = Color.decode("#FFFFFF");

    private final Font font_button = new Font("Segoe UI Symbol", Font.BOLD, 30); 
    private final Font font_display = new Font("Segoe UI Symbol", Font.BOLD, 36);

    private final Map<String, Runnable> actions = new HashMap<>();
    private final StringBuilder display = new StringBuilder("0");
    private final StringBuilder expression = new StringBuilder();
    private boolean isNewEquation = false;
    
    private Runnable onCalculateAction;
    private Runnable onUpdate;

    private final String[][] buttonLabels = {
        {"sin",   "cos",   "tan",   "asin",  "acos",  "atan",  "sinh",  "cosh"},
        {"tanh",  "asinh", "acosh", "atanh", "log",   "ln",    "log₂",  "√x"},
        
        {"e^x",   "10^x",  "x²",    "7",     "8",     "9",     "DEL",   "AC"},
        {"e",     "π",     "abs",   "4",     "5",     "6",     "×",     "÷"},
        {"(",     ")",     "x^y",   "1",     "2",     "3",     "+",     "−"},
        {"x!",    "mod",   "x⁻¹",   "0",     ".",     "%",     "ANS",   "="}
    };

    public CalcKeys() {
        initializeActions(); 

        setLayout(new GridBagLayout());
        setBackground(bg_color);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(6, 6, 6, 6); 

        for (int row = 0; row < buttonLabels.length; row++) {
            for (int col = 0; col < buttonLabels[row].length; col++) {
                String text = buttonLabels[row][col];

                gbc.gridx = col;
                gbc.gridy = row;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                
                if (text.equals("=")) {
                    gbc.gridwidth = 2; 
                } else {
                    gbc.gridwidth = 1;
                }
                
                RoundButton button = createThemedButton(text);
                add(button, gbc);
            }
        }
    }

    private void initializeActions() {
        String[] simple = {"0","1","2","3","4","5","6","7","8","9",".","(",")","+","-","*","/"};
        for (String s : simple) actions.put(s, () -> addToState(s, s));

        actions.put("π",   () -> addToState("π", "pi"));
        actions.put("e",   () -> addToState("e", "e"));
        actions.put("×",   () -> addToState("×", "*"));
        actions.put("÷",   () -> addToState("÷", "/"));
        actions.put("−",   () -> addToState("−", "-"));
        actions.put("ANS", () -> addToState("Ans", "ans"));

        actions.put("sin",   () -> addToState("sin(",   "sin("));
        actions.put("cos",   () -> addToState("cos(",   "cos("));
        actions.put("tan",   () -> addToState("tan(",   "tan("));
        actions.put("asin",  () -> addToState("asin(",  "asin("));
        actions.put("acos",  () -> addToState("acos(",  "acos("));
        actions.put("atan",  () -> addToState("atan(",  "atan("));
        actions.put("sinh",  () -> addToState("sinh(",  "sinh("));
        actions.put("cosh",  () -> addToState("cosh(",  "cosh("));
        actions.put("tanh",  () -> addToState("tanh(",  "tanh("));
        actions.put("asinh", () -> addToState("asinh(", "asinh("));
        actions.put("acosh", () -> addToState("acosh(", "acosh("));
        actions.put("atanh", () -> addToState("atanh(", "atanh("));

        actions.put("log",   () -> addToState("log₁₀(", "logten("));
        actions.put("ln",    () -> addToState("ln(",    "log("));
        actions.put("log₂",  () -> addToState("log₂(",  "logtwo("));
        actions.put("e^x",   () -> addToState("e^",     "exp("));
        actions.put("10^x",  () -> addToState("10^",    "10^("));

        actions.put("x²",   () -> addToState("²",       "^2"));
        actions.put("x^y",  () -> addToState("^",       "^"));
        actions.put("x!",   () -> addToState("!",       "!"));
        actions.put("√x",   () -> addToState("√(",      "sqrt("));
        actions.put("abs",  () -> addToState("abs(",    "abs("));
        actions.put("mod",  () -> addToState(" mod ",   "%"));
        actions.put("x⁻¹",  () -> addToState("⁻¹",      "^(-1)"));
        actions.put("%",    () -> addToState("%",       "/100"));

        actions.put("AC",  this::clearDisplay);
        actions.put("DEL", this::handleBackspace); 
        
        actions.put("=", () -> {
            if (onCalculateAction != null) {
                onCalculateAction.run();
            }
        });
    }

    private void addToState(String uiStr, String logicStr) {
        if (isNewEquation) {
            isNewEquation = false;
            
            if (isOperator(logicStr)) {
                if (display.length() == 0) {
                    display.append("Ans");
                    expression.append("Ans");
                }
            } else {
                clearDisplay();
            }
        }
        
        if (display.toString().equals("0") && !uiStr.equals(".")) {
            if (!isOperator(logicStr)) {
                display.setLength(0);
                expression.setLength(0);
            }
        }
        
        display.append(uiStr);
        expression.append(logicStr);

        triggerUpdate();
    }

    private boolean isOperator(String logicStr) {
        return logicStr.startsWith("+") || logicStr.startsWith("-") || 
               logicStr.startsWith("*") || logicStr.startsWith("/") || 
               logicStr.startsWith("^") || logicStr.startsWith("!") || 
               logicStr.startsWith("%");
    }

    private void clearDisplay() {
        display.setLength(0);
        display.append("0");
        expression.setLength(0);
        isNewEquation = false;
        triggerUpdate();
    }

    private void handleBackspace() {
        if (display.length() > 0 && !display.toString().equals("0")) {
            int displayDrop = getDeleteSize(display.toString());
            display.setLength(display.length() - displayDrop);
        }
        
        if (display.length() == 0) {
            display.append("0"); 
        }
        
        if (expression.length() > 0) {
            int exprDrop = getDeleteSize(expression.toString());
            expression.setLength(expression.length() - exprDrop);
        }
        
        triggerUpdate();
    }

    private int getDeleteSize(String text) {
        String[] wholeWords = {
            "asinh(", "acosh(", "atanh(", "log₁₀(",
            "asin(", "acos(", "atan(", "sinh(", "cosh(", "tanh(", "log₂(", "log2(", "sqrt(",
            "sin(", "cos(", "tan(", "log(", "abs(",
            "ln(", "Ans",
            "√("
        };
        
        for (String word : wholeWords) {
            if (text.endsWith(word)) {
                return word.length(); 
            }
        }
        
        return 1; 
    }

    private RoundButton createThemedButton(String text) {
        RoundButton btn = new RoundButton(text, 15);
        
        btn.addActionListener(e -> {
            Runnable action = actions.get(text);
            if (action != null) {
                action.run();
            }
        });
        
        if (text.equals("AC") || text.equals("DEL")) {
            btn.setBackground(danger_color);
            btn.setForeground(text_primary);
            btn.setFont(font_button);
        } else if (text.matches("[0-9]") || text.equals(".")) {
            btn.setBackground(num_bg_color);
            btn.setForeground(text_primary);
            btn.setFont(font_display);
        } else if (text.equals("=")) {
            btn.setBackground(accent_color);
            btn.setForeground(bg_color); 
            btn.setFont(font_display);
        } else {
            btn.setBackground(panel_color);
            btn.setForeground(accent_color);
            btn.setFont(font_button);
        }
        
        return btn;
    }

    private void triggerUpdate() {
        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    public void clearText() {
        display.setLength(0);
        expression.setLength(0);
    }
    
    public void setOnCalculate(Runnable action) {
        this.onCalculateAction = action;
    }

    public void setOnUpdate(Runnable action) {
        this.onUpdate = action;
    }

    public String getDisplayString() {
        return display.toString();
    }

    public String getExpressionString() {
        return expression.toString();
    }
    
    public void setReadyForNewEquation() {
        this.isNewEquation = true;
    }
}