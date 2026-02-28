package gui;

import gui.elements.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class StatisticsPage extends JPanel {
    private final Color BACKGROUND = Color.decode("#0B1A1E");
    private final Color ACCENT = Color.decode("#00d9ff");
    private final Color TEXT_DIM = Color.decode("#7A8C8F");
    
    private BoxPlot boxPlot;
    private Histogram histogram;
    private ScatterPlot scatterPlot;
    private ActiveDatasetTable datasetTable;
    private StatisticalAnalysisPanel analysisPanel;
    private List<Double> dataReference;
    private JPanel plotCards;
    private CardLayout cardLayout;
    private JPanel tabSwitcher; 

    public StatisticsPage(List<Double> data) {
        this.dataReference = data;
        setLayout(new BorderLayout(20, 0));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel centerContent = new JPanel(new BorderLayout(0, 20));
        centerContent.setOpaque(false);
        centerContent.add(createGraphContainer(), BorderLayout.CENTER);

        this.datasetTable = new ActiveDatasetTable(dataReference);
        this.datasetTable.setPreferredSize(new Dimension(0, 320)); 
        centerContent.add(datasetTable, BorderLayout.SOUTH);

        JPanel sidebarColumn = new JPanel();
        sidebarColumn.setLayout(new BoxLayout(sidebarColumn, BoxLayout.Y_AXIS));
        sidebarColumn.setOpaque(false);
        sidebarColumn.setPreferredSize(new Dimension(300, 0));
        this.analysisPanel = new StatisticalAnalysisPanel(dataReference);
        sidebarColumn.add(analysisPanel);
        
        setupLinking();
        add(centerContent, BorderLayout.CENTER);
        add(sidebarColumn, BorderLayout.EAST);
    }

    private JPanel createGraphContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        this.tabSwitcher = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        tabSwitcher.setBackground(BACKGROUND);
        tabSwitcher.setOpaque(true);

        this.cardLayout = new CardLayout();
        this.plotCards = new JPanel(cardLayout);
        plotCards.setOpaque(false);

        this.boxPlot = new BoxPlot(dataReference);
        this.histogram = new Histogram(dataReference);
        this.scatterPlot = new ScatterPlot(dataReference);

        plotCards.add(scatterPlot, "SCATTER");
        plotCards.add(histogram, "HIST");
        plotCards.add(boxPlot, "BOX");

        tabSwitcher.add(createTabButton("Scatter Plot", "SCATTER"));
        tabSwitcher.add(createTabButton("Histogram", "HIST"));
        tabSwitcher.add(createTabButton("Box Plot", "BOX"));

        container.add(tabSwitcher, BorderLayout.NORTH);
        container.add(plotCards, BorderLayout.CENTER);
        
        showChart("BOX"); 
        return container;
    }

    private JButton createTabButton(String text, String cardKey) {
        JButton btn = new JButton(text.toUpperCase());
        btn.setFont(new Font("SansSerif", Font.BOLD, 10));
        
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setContentAreaFilled(false);
        btn.setRolloverEnabled(false);
        btn.setOpaque(false);
        
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> showChart(cardKey));
        
        return btn;
    }

    public void showChart(String cardKey) {
        cardLayout.show(plotCards, cardKey);
        
        for (Component c : tabSwitcher.getComponents()) {
            if (c instanceof JButton b) {
                boolean active = getCardKeyFromText(b.getText()).equals(cardKey);
                
                b.setForeground(active ? ACCENT : TEXT_DIM);
                b.setContentAreaFilled(active);
                b.setOpaque(active);
                
                if (active) {
                    b.setBackground(new Color(0, 217, 255, 30));
                    b.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT, 1, true), 
                        new EmptyBorder(5, 15, 5, 15)));
                } else {
                    b.setBackground(BACKGROUND);
                    b.setBorder(new EmptyBorder(6, 16, 6, 16));
                }
            }
        }
        tabSwitcher.revalidate();
        tabSwitcher.repaint();
    }

    private String getCardKeyFromText(String text) {
        if (text.contains("SCATTER")) return "SCATTER";
        if (text.contains("HISTOGRAM")) return "HIST";
        return "BOX";
    }

    private void setupLinking() {
        analysisPanel.addRecalculateListener(e -> {
            boxPlot.repaint(); histogram.repaint(); scatterPlot.repaint();
            datasetTable.refreshTable();
        });
    }
}