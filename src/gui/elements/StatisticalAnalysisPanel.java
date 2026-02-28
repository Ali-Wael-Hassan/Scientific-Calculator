package gui.elements;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticalAnalysisPanel extends JPanel {
    private final Color BACKGROUND = Color.decode("#0B1A1E");
    private final Color CARD_BG = Color.decode("#122529");
    private final Color ACCENT = Color.decode("#00d9ff");
    private final Color TEXT_MAIN = Color.decode("#FFFFFF");
    private final Color TEXT_DIM = Color.decode("#7A8C8F");

    private final List<Double> dataReference;
    private final RoundButton recalculateButton;

    public StatisticalAnalysisPanel(List<Double> data) {
        this.dataReference = data;
        
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setPreferredSize(new Dimension(300, 0));

        this.recalculateButton = createRecalculateButton();
        setupInternalListener();

        refreshUI();
    }

    public void addRecalculateListener(ActionListener listener) {
        recalculateButton.addActionListener(listener);
    }

    private void setupInternalListener() {
        recalculateButton.addActionListener(e -> refreshUI());
    }

    public void refreshUI() {
        removeAll();
        
        add(createHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBackground(BACKGROUND);

        List<Double> validData = dataReference.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (!validData.isEmpty()) {
            double mean = validData.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double sum = validData.stream().mapToDouble(Double::doubleValue).sum();
            double median = calculateMedian(validData);
            double stdDev = calculateStdDev(validData, mean);
            String modeText = calculateMode(validData);

            content.add(createStatCard("MEAN (M)", String.format("%.4f", mean), null, true));
            content.add(Box.createVerticalStrut(15));
            
            JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
            row.setOpaque(false);
            row.add(createStatCard("MEDIAN", String.format("%.3f", median), null, false));
            row.add(createStatCard("MODE", modeText, null, false));
            content.add(row);
            
            content.add(Box.createVerticalStrut(15));
            
            content.add(createStatCard("STD DEVIATION (Σ)", String.format("%.3f", stdDev), "Var: " + String.format("%.3f", stdDev * stdDev), false));
            content.add(Box.createVerticalStrut(15));
            content.add(createStatCard("SUM (ΣX)", String.format("%.3f", sum), null, false));
        } else {
            content.add(createStatCard("MEAN (M)", "No Data", null, true));
            content.add(Box.createVerticalStrut(15));
            
            JPanel row = new JPanel(new GridLayout(1, 2, 15, 0));
            row.setOpaque(false);
            row.add(createStatCard("MEDIAN", "No Data", null, false));
            row.add(createStatCard("MODE", "No Data", null, false));
            content.add(row);
            
            content.add(Box.createVerticalStrut(15));
            content.add(createStatCard("STD DEVIATION (Σ)", "No Data", "Var: N/A", false));
            content.add(Box.createVerticalStrut(15));
            content.add(createStatCard("SUM (ΣX)", "No Data", null, false));
        }

        add(content, BorderLayout.CENTER);
        
        add(recalculateButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JLabel createHeader() {
        JLabel title = new JLabel("STATISTICAL ANALYSIS");
        title.setForeground(ACCENT);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        return title;
    }

    private JPanel createStatCard(String label, String value, String subValue, boolean hasAccent) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (hasAccent) {
                    g2.setColor(ACCENT);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lbl = new JLabel(label);
        lbl.setForeground(TEXT_DIM);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        
        JLabel val = new JLabel(value);
        val.setForeground(TEXT_MAIN);
        val.setFont(new Font("Monospaced", Font.BOLD, 22));

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);

        if (subValue != null) {
            JLabel sub = new JLabel(subValue);
            sub.setForeground(ACCENT.darker());
            sub.setFont(new Font("SansSerif", Font.PLAIN, 10));
            card.add(sub, BorderLayout.SOUTH);
        }

        return card;
    }

    private RoundButton createRecalculateButton() {
        RoundButton btn = new RoundButton("Recalculate", 15);
        btn.setPreferredSize(new Dimension(0, 55));
        btn.setBackground(ACCENT);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBloomEnabled(true);
        btn.setBloomLevel(6);
        btn.setBloomColor(ACCENT);
        return btn;
    }

    private String calculateMode(List<Double> data) {
        if (data == null || data.isEmpty()) return "N/A";
        Map<Double, Integer> frequencies = new HashMap<>();
        for (Double d : data) frequencies.put(d, frequencies.getOrDefault(d, 0) + 1);

        int maxFreq = Collections.max(frequencies.values());
        if (maxFreq <= 1) return "N/A";

        List<Double> modes = frequencies.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFreq)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (modes.size() == frequencies.size()) return "N/A";
        return modes.size() == 1 ? String.format("%.2f", modes.get(0)) : "Multiple";
    }

    private double calculateMedian(List<Double> data) {
        List<Double> s = new ArrayList<>(data);
        Collections.sort(s);
        int size = s.size();
        if (size == 0) return 0;
        if (size % 2 == 0) return (s.get(size/2 - 1) + s.get(size/2)) / 2.0;
        return s.get(size/2);
    }

    private double calculateStdDev(List<Double> data, double mean) {
        double variance = data.stream()
                .mapToDouble(d -> Math.pow(d - mean, 2))
                .average().orElse(0.0);
        return Math.sqrt(variance);
    }
}