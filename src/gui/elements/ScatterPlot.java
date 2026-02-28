package gui.elements;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ScatterPlot extends JPanel {
    private List<Double> table;

    private final int PADDING = 50;
    private final Color GRID_COLOR = Color.decode("#1C363B");
    private final Color AXIS_COLOR = Color.decode("#11b2fd");
    private final Color DOT_COLOR = Color.decode("#00d9ff");

    // View State
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

        double buffer = rawRange * 0.15;
        this.viewMin = rawMin - buffer;
        this.viewMax = rawMax + buffer;
        this.viewRange = viewMax - viewMin;
    }

    private void drawGrid(Graphics2D g2) {
        int w = getWidth(), h = getHeight();
        g2.setColor(GRID_COLOR);
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
        
        g2.drawLine(PADDING, PADDING, PADDING, h - PADDING);
        g2.drawLine(PADDING, h - PADDING, w - PADDING, h - PADDING);

        int divisions = 10;
        int lastYVal = -1;
        FontMetrics fm = g2.getFontMetrics();

        for (int i = 0; i <= divisions; i++) {
            int yFreq = (i * maxFreq) / divisions;
            int yPos = (h - PADDING) - (i * (h - 2 * PADDING) / divisions);
            if (yFreq != lastYVal || i == 0) {
                String yLab = String.valueOf(yFreq);
                g2.drawString(yLab, PADDING - fm.stringWidth(yLab) - 8, yPos + 4);
                lastYVal = yFreq;
            }

            double val = viewMin + (i * viewRange / divisions);
            String xLab = String.format("%.1f", val);
            int xPos = PADDING + (i * (w - 2 * PADDING) / divisions);
            
            if (i % 2 == 0) {
                g2.drawString(xLab, xPos - (fm.stringWidth(xLab) / 2), h - PADDING + 20);
            }
            g2.drawLine(xPos, h - PADDING, xPos, h - PADDING + 3);
        }
    }

    private void drawData(Graphics2D g2) {
        int w = getWidth() - 2 * PADDING;
        int h = getHeight() - 2 * PADDING;
        int dotSize = 10;

        g2.setColor(DOT_COLOR);
        
        for (Map.Entry<Double, Integer> entry : frequencies.entrySet()) {
            double xVal = entry.getKey();
            int yVal = entry.getValue();

            double xRatio = (xVal - viewMin) / viewRange;
            double yRatio = (double) yVal / maxFreq;

            int px = PADDING + (int)(xRatio * w);
            int py = (getHeight() - PADDING) - (int)(yRatio * h);

            g2.setColor(new Color(0, 217, 255, 100));
            g2.fillOval(px - (dotSize/1), py - (dotSize/1), dotSize*2, dotSize*2);
            
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
        g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, 
                     (getHeight() / 2) + (fm.getAscent() / 2) - (fm.getDescent() / 2));
        g2.dispose();
    }
}