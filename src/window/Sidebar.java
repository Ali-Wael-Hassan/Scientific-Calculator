package window;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Cursor;

public class Sidebar extends JPanel {

    private JPanel cardContainer;

    public Sidebar() {
        // Init Sidebar
        setBackground(StyleConfig.SIDEBAR_BG);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 0));
        setBorder(StyleConfig.MARGIN);

        // Header Section
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("HISTORY");
        title.setForeground(StyleConfig.ACCENT_CYAN.darker());

        JLabel clearAll = new JLabel("Clear All");
        clearAll.setForeground(Color.GRAY);
        clearAll.setCursor(new Cursor(Cursor.HAND_CURSOR));

        clearAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearHistory();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                clearAll.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                clearAll.setForeground(Color.GRAY);
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(clearAll, BorderLayout.EAST);
        // Add vertical gap
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        add(header, BorderLayout.NORTH);

        // Card Container
        cardContainer = new JPanel();
        cardContainer.setLayout(new BoxLayout(cardContainer, BoxLayout.Y_AXIS));
        cardContainer.setOpaque(false);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(cardContainer);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Hide scrollbars until needed
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void addHistoryEntry(String expression, String result) {
        // Add spacing between cards if one already exists
        if (cardContainer.getComponentCount() > 0) {
            cardContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        cardContainer.add(new SidebarCard(expression, result, StyleConfig.ACCENT_CYAN));
        
        // Rcalculating Layouts and Repaint
        cardContainer.revalidate();
        cardContainer.repaint();
    }

    public void clearHistory() {
        cardContainer.removeAll();
        cardContainer.revalidate();
        cardContainer.repaint();
    }
}