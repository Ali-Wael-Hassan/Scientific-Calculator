package gui.elements;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Histogram extends JPanel {
    private List<Double> table;

    private final int PADDING = 50;
    private final Color GRID_COLOR = Color.decode("#1C363B");
    private final Color AXIS_COLOR = Color.decode("#11b2fd");
    private final Color BAR_COLOR = new Color(0, 217, 255, 180); 
    private final Color BAR_BORDER = Color.decode("#00d9ff");
    private final int BINS = 10;

    private double viewMin, viewMax, viewRange;
    private double rawMin, rawMax, rawRange;
    private int maxFreq;
    private int[] counts;

    public Histogram(List<Double> table) {
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
        this.rawMin = data.stream().min(Double::compare).orElse(0.0);
        this.rawMax = data.stream().max(Double::compare).orElse(1.0);
        this.rawRange = Math.max(0.1, rawMax - rawMin);

        double buffer = rawRange * 0.15;
        this.viewMin = rawMin - buffer;
        this.viewMax = rawMax + buffer;
        this.viewRange = viewMax - viewMin;

        this.counts = new int[BINS];
        double binSize = rawRange / BINS;
        for (double val : data) {
            int binIdx = (int) ((val - rawMin) / binSize);
            if (binIdx >= BINS) binIdx = BINS - 1;
            if (binIdx < 0) binIdx = 0;
            counts[binIdx]++;
        }

        this.maxFreq = 0;
        for (int c : counts) if (c > maxFreq) maxFreq = c;
        if (maxFreq == 0) maxFreq = 1;
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
        
        g2.drawLine(PADDING, PADDING, PADDING, h - PADDING);
        g2.drawLine(PADDING, h - PADDING, w - PADDING, h - PADDING);

        int divisions = 13;
        FontMetrics fm = g2.getFontMetrics();
        int lastYVal = -1;

        for (int i = 0; i <= divisions; i++) {
            int yFreq = (i * maxFreq) / divisions;
            int yPos = (h - PADDING) - (i * (h - 2 * PADDING) / divisions);

            if (yFreq != lastYVal || i == 0) {
                String yLab = String.valueOf(yFreq);
                g2.drawString(yLab, PADDING - fm.stringWidth(yLab) - 8, yPos + 4);
                lastYVal = yFreq;
            }

            g2.drawLine(PADDING - 3, yPos, PADDING, yPos);

            double val = viewMin + (i * viewRange / divisions);
            String xLab = String.format("%.1f", val);
            int xPos = PADDING + (i * (w - 2 * PADDING) / divisions);

            g2.drawString(xLab, xPos - (fm.stringWidth(xLab) / 2), h - PADDING + 20);
            g2.drawLine(xPos, h - PADDING, xPos, h - PADDING + 3);
        }
    }

    private void drawData(Graphics2D g2) {
        int w = getWidth() - 2 * PADDING;
        int h = getHeight() - 2 * PADDING;
        
        double barWidthPx = (rawRange / BINS) / viewRange * w;

        for (int i = 0; i < BINS; i++) {
            int barH = (int) (((double) counts[i] / maxFreq) * h);
            
            double binStartVal = rawMin + (i * (rawRange / BINS));
            double xRatio = (binStartVal - viewMin) / viewRange;
            int pixelX = PADDING + (int)(xRatio * w);
            int pixelY = (getHeight() - PADDING) - barH;

            // Render
            g2.setColor(BAR_COLOR);
            g2.fillRect(pixelX, pixelY, (int)barWidthPx + 1, barH);

            g2.setColor(BAR_BORDER);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRect(pixelX, pixelY, (int)barWidthPx + 1, barH);
        }
    }

    private void drawPlaceholder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.decode("#7A8C8F"));
        g2.setFont(new Font("SansSerif", Font.ITALIC, 14));
        String msg = "Awaiting valid dataset...";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(msg)) / 2;
        int y = (getHeight() / 2) + (fm.getAscent() / 2) - (fm.getDescent() / 2);
        g2.drawString(msg, x, y);
        g2.dispose();
    }
}