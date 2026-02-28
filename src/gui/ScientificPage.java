package gui;

import javax.swing.*;
import java.awt.*;
import gui.elements.ResultPanel;
import gui.elements.CalcKeys;
import core.MathEngine;
import java.text.DecimalFormat;

public class ScientificPage extends JPanel {
    
    private final Color bg_color = Color.decode("#0B1619"); 
    
    private final ResultPanel resultPanel;
    private final CalcKeys keysPanel;

    public ScientificPage() {
        setLayout(new BorderLayout(0, 0));
        setBackground(bg_color);

        resultPanel = new ResultPanel(); 
        resultPanel.setPreferredSize(new Dimension(0, 250));
        keysPanel = new CalcKeys();

        add(resultPanel, BorderLayout.NORTH);
        add(keysPanel, BorderLayout.CENTER);

        keysPanel.setOnUpdate(() -> {
            resultPanel.setResultText("= " + keysPanel.getDisplayString());
        });

        keysPanel.setOnCalculate(() -> {
            String engineMath = keysPanel.getExpressionString();
            String displayMath = keysPanel.getDisplayString();
            
            try {
                double res = MathEngine.getInstance().calculate(engineMath.toLowerCase());
                
                DecimalFormat df = new DecimalFormat("#.######");
                String resStr = df.format(res);
                
                resultPanel.setEquationText(displayMath);
                resultPanel.setResultText("= " + resStr);
                
                keysPanel.clearText(); 
                keysPanel.setReadyForNewEquation(); 
                
            } catch (Exception e) {
                resultPanel.setEquationText(displayMath + " =");
                resultPanel.setResultText("= Error");
                keysPanel.setReadyForNewEquation();
            }
        });
    }
}