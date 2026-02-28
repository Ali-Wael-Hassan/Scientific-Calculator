package gui.elements;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ResultPanel extends JPanel {
    private JLabel expression; // Fixed typo
    private BloomLabel result;
    
    public ResultPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(40, 30, 40, 30));

        expression = new JLabel("");
        expression.setHorizontalAlignment(SwingConstants.RIGHT);
        
        result = new BloomLabel("0", SwingConstants.RIGHT);

        expression.setFont(expression.getFont().deriveFont(40f));
        result.setFont(result.getFont().deriveFont(80f));

        add(expression, BorderLayout.NORTH);
        add(result, BorderLayout.SOUTH);
    }

    public void setResultText(String resultText) {
        result.setText(resultText);
    }

    public void setEquationText(String equationText) {
        expression.setText(equationText);
    }
    
    public JLabel getExpressionLabel() { return expression; }
    public BloomLabel getResultLabel() { return result; }
}