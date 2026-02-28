package gui.elements;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BoxPlot extends JPanel {
    private List<Double> table;
    
    private double lb, ub, q1, q3, median;
    private double viewMin, viewMax, viewRange;

    private final int PADDING = 60;
    private final Color GRID_COLOR = Color.decode("#1C363B");
    private final Color AXIS_COLOR = Color.decode("#11b2fd");
    private final Color DATA_COLOR = Color.decode("#00d9ff");
    private final Color TEXT_DIM = Color.decode("#7A8C8F");

    public BoxPlot(List<Double> table) {
        this.table = table;
        setOpaque(false);
    }

    private boolean calculateStatistics(List<Double> validData) {
        if (validData.isEmpty()) return false;
        
        List<Double> s = new ArrayList<>(validData);
        Collections.sort(s);

        this.q1 = getPercentile(s, 25);
        this.median = getPercentile(s, 50);
        this.q3 = getPercentile(s, 75);

        double iqr = q3 - q1;
        this.lb = q1 - 1.5 * iqr;
        this.ub = q3 + 1.5 * iqr;

        double rawRange = Math.max(0.1, ub - lb);
        double buffer = rawRange * 0.15;
        
        this.viewMin = lb - buffer;
        this.viewMax = ub + buffer;
        this.viewRange = this.viewMax - this.viewMin;
        return true;
    }

    private double getPercentile(List<Double> s, double percentile) {
        if (s.size() == 1) return s.get(0);
        double index = (percentile / 100.0) * (s.size() - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        if (lower == upper) return s.get(lower);
        return s.get(lower) + (index - lower) * (s.get(upper) - s.get(lower));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        List<Double> validData = table.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (validData.isEmpty()) {
            drawPlaceholder(g2);
            return;
        }

        if (!calculateStatistics(validData)) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawAxis(g2);
        drawData(g2);

        g2.dispose();
    }

    private void drawPlaceholder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(TEXT_DIM);
        g2.setFont(new Font("SansSerif", Font.ITALIC, 14));
        
        String msg = "Awaiting valid dataset input...";
        FontMetrics fm = g2.getFontMetrics();
        
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        
        int y = (getHeight() / 2) + (fm.getAscent() / 2) - (fm.getDescent() / 2);
        
        g2.drawString(msg, x, y);
        g2.dispose();
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
        int h = getHeight();
        g2.setColor(AXIS_COLOR);
        g2.setStroke(new BasicStroke(2f));
        
        g2.drawLine(PADDING, h - PADDING, getWidth() - PADDING, h - PADDING);
        g2.drawLine(PADDING, h - PADDING, PADDING, PADDING);

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        for (int i = 0; i <= 5; i++) {
            double val = viewMin + (i * viewRange / 5);
            int yPixel = (h - PADDING) - (i * (h - 2 * PADDING) / 5);
            String label = String.format("%.1f", val);
            g2.drawString(label, PADDING - g2.getFontMetrics().stringWidth(label) - 10, yPixel + 5);
        }
    }

    private void drawData(Graphics2D g2) {
        int h = getHeight() - 2 * PADDING;
        int centerX = PADDING + ((getWidth() - 2 * PADDING) / 2);
        int boxWidth = 60;

        int yLB = toPixel(lb, h);
        int yUB = toPixel(ub, h);
        int yQ1 = toPixel(q1, h);
        int yQ3 = toPixel(q3, h);
        int yMed = toPixel(median, h);

        g2.setColor(DATA_COLOR);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(centerX, yUB, centerX, yLB);
        g2.drawLine(centerX - 15, yUB, centerX + 15, yUB);
        g2.drawLine(centerX - 15, yLB, centerX + 15, yLB);

        int boxHeight = Math.max(2, yQ1 - yQ3); 
        g2.setColor(new Color(0, 217, 255, 40));
        g2.fillRect(centerX - boxWidth / 2, yQ3, boxWidth, boxHeight);
        
        g2.setColor(DATA_COLOR);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(centerX - boxWidth / 2, yQ3, boxWidth, boxHeight);

        g2.setStroke(new BasicStroke(3f)); 
        g2.drawLine(centerX - boxWidth / 2, yMed, centerX + boxWidth / 2, yMed);
    }

    private int toPixel(double value, int usableHeight) {
        if (viewRange == 0) return getHeight() / 2;
        double ratio = (value - viewMin) / viewRange;
        return (getHeight() - PADDING) - (int) (ratio * usableHeight);
    }
}