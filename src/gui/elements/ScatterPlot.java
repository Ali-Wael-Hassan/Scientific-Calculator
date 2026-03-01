package gui.elements;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ScatterPlot extends JPanel {
    private List<Double> table;

    private final int PADDING = 60;
    private final Color GRID_COLOR = Color.decode("#1C363B");
    private final Color AXIS_COLOR = Color.decode("#11b2fd");
    private final Color DOT_COLOR = Color.decode("#00d9ff");

    private double viewMin, viewMax, viewRange;
    private int maxFreq;
    private Map<Double, Integer> frequencies;

    public ScatterPlot(List<Double> table) {
        this.table = table;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        List<Double> validData = table.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (validData.isEmpty()) {
            drawPlaceholder(g);
            return;
        }

        calculateBoundsAndFrequencies(validData);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawAxis(g2);
        drawData(g2);

        g2.dispose();
    }

    private void calculateBoundsAndFrequencies(List<Double> data) {
        this.frequencies = new HashMap<>();
        for (Double val : data) {
            frequencies.put(val, frequencies.getOrDefault(val, 0) + 1);
        }
        
        this.maxFreq = frequencies.values().stream().max(Integer::compare).orElse(1);

        double rawMin = data.stream().min(Double::compare).orElse(0.0);
        double rawMax = data.stream().max(Double::compare).orElse(1.0);
        
        double rawRange = Math.max(0.1, rawMax - rawMin);
        double buffer = rawRange * 0.1;
        this.viewMin = rawMin - buffer;
        this.viewMax = rawMax + buffer;
        this.viewRange = viewMax - viewMin;
    }

    private void drawGrid(Graphics2D g2) {
        int w = getWidth(), h = getHeight();
        g2.setColor(GRID_COLOR);
        g2.setStroke(new BasicStroke(1f));
        
        for (int i = 0; i <= 10; i++) {
            int x = PADDING + (i * (w - 2 * PADDING) / 10);
            int y = PADDING + (i * (h - 2 * PADDING) / 10);
            g2.drawLine(x, PADDING, x, h - PADDING);
            g2.drawLine(PADDING, y, w - PADDING, y);
        }
    }

    private void drawAxis(Graphics2D g2) {
        int w = getWidth(), h = getHeight();
        g2.setColor(AXIS_COLOR);
        g2.setStroke(new BasicStroke(2f));
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();

        g2.drawLine(PADDING, PADDING, PADDING, h - PADDING);
        g2.drawLine(PADDING, h - PADDING, w - PADDING, h - PADDING);

        int yDivisions = Math.min(maxFreq, 10);
        for (int i = 0; i <= yDivisions; i++) {
            int freqValue = (int) Math.round((double) i * maxFreq / yDivisions);
            int yPos = (h - PADDING) - (int) ((double) freqValue / maxFreq * (h - 2 * PADDING));
            
            String label = String.valueOf(freqValue);
            g2.drawString(label, PADDING - fm.stringWidth(label) - 10, yPos + 5);
            g2.drawLine(PADDING - 3, yPos, PADDING, yPos);
        }

        for (int i = 0; i <= 10; i++) {
            double val = viewMin + (i * viewRange / 10);
            String xLab = String.format("%.1f", val);
            int xPos = PADDING + (i * (w - 2 * PADDING) / 10);
            
            if (i % 2 == 0) {
                g2.drawString(xLab, xPos - (fm.stringWidth(xLab) / 2), h - PADDING + 20);
            }
            g2.drawLine(xPos, h - PADDING, xPos, h - PADDING + 5);
        }
        
        g2.drawString("FREQUENCY", 10, PADDING - 10);
        g2.drawString("VALUE", w - PADDING - 20, h - PADDING + 35);
    }

    private void drawData(Graphics2D g2) {
        int graphW = getWidth() - 2 * PADDING;
        int graphH = getHeight() - 2 * PADDING;
        int dotSize = 8;

        for (Map.Entry<Double, Integer> entry : frequencies.entrySet()) {
            double xValue = entry.getKey();
            int frequency = entry.getValue();

            double xRatio = (xValue - viewMin) / viewRange;
            double yRatio = (double) frequency / maxFreq;

            int px = PADDING + (int) (xRatio * graphW);
            int py = (getHeight() - PADDING) - (int) (yRatio * graphH);

            g2.setColor(new Color(0, 217, 255, 60));
            g2.fillOval(px - 6, py - 6, 12, 12);
            
            g2.setColor(DOT_COLOR);
            g2.fillOval(px - (dotSize/2), py - (dotSize/2), dotSize, dotSize);
        }
    }

    private void drawPlaceholder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.decode("#7A8C8F"));
        g2.setFont(new Font("SansSerif", Font.ITALIC, 14));
        String msg = "Awaiting valid dataset...";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
        g2.dispose();
    }
}