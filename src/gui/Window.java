package gui;

import javax.swing.*;
import javax.swing.border.*;
import core.MathEngine;
import java.awt.*;

public class Window extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel container = new JPanel(cards);
    private final JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
    private final Color NAV_BG = Color.decode("#0B1619"), ACCENT = Color.decode("#00d9ff");

    public Window() {
        ImageIcon img = new ImageIcon("assets/graphics/icon.png");
        this.setIconImage(img.getImage());
        
        setTitle("Quantum Calculator");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(NAV_BG);

        container.setOpaque(false);
        container.add(new ScientificPage(), "Scientific");
        container.add(new StatisticsPage(MathEngine.getInstance().getTable()), "Statistics");

        navBar.setBackground(NAV_BG);
        navBar.setOpaque(true); 
        navBar.setBorder(new EmptyBorder(10, 0, 10, 0));

        for (String tab : new String[]{"Scientific", "Statistics"}) {
            JButton b = new JButton(tab);
            b.setFont(new Font("SansSerif", Font.BOLD, 13));
            
            b.setFocusPainted(false);
            b.setBorderPainted(true);
            b.setContentAreaFilled(false);
            b.setRolloverEnabled(false); 
            
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.addActionListener(e -> showPage(tab));
            navBar.add(b);
        }

        setLayout(new BorderLayout());
        add(navBar, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
        
        showPage("Scientific");
        setVisible(true);
    }

    public void showPage(String name) {
        cards.show(container, name);
        for (Component c : navBar.getComponents()) {
            if (c instanceof JButton b) {
                boolean active = b.getText().equals(name);
                
                b.setOpaque(active);
                b.setContentAreaFilled(active); 
                b.setForeground(active ? ACCENT : Color.LIGHT_GRAY);
                
                if (active) {
                    b.setBackground(new Color(0, 217, 255, 30));
                    b.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT, 1, true), 
                        new EmptyBorder(5, 15, 5, 15)));
                } else {
                    b.setBackground(NAV_BG); 
                    b.setBorder(new EmptyBorder(6, 16, 6, 16));
                }
            }
        }
        navBar.revalidate();
        navBar.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Window::new);
    }
}