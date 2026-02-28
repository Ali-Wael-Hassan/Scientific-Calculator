package gui.elements;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ActiveDatasetTable extends JPanel {
    private final Color BACKGROUND = Color.decode("#0B1A1E");
    private final Color HEADER_BG = Color.decode("#122529");
    private final Color GRID_COLOR = Color.decode("#1C363B");
    private final Color ACCENT = Color.decode("#00d9ff");
    private final Color TEXT_MAIN = Color.decode("#FFFFFF");
    private final Color TEXT_DIM = Color.decode("#7A8C8F");

    private JTable table;
    private DefaultTableModel model;
    private final List<Double> dataReference;
    private boolean isRefreshing = false;

    public ActiveDatasetTable(List<Double> dataReference) {
        this.dataReference = dataReference;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        
        add(createTopPanel(), BorderLayout.NORTH);

        setupTable();
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRID_COLOR));
        add(scrollPane, BorderLayout.CENTER);
        
        refreshTable();
    }

    private JPanel createTopPanel() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("ACTIVE DATASET");
        title.setForeground(TEXT_DIM);
        title.setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        RoundButton addBtn = new RoundButton("+ ADD ROW", 8);
        styleToolbarButton(addBtn, ACCENT);
        addBtn.addActionListener(e -> {
            dataReference.add(0.0);
            refreshTable();
        });

        RoundButton clearBtn = new RoundButton("CLEAR", 8);
        styleToolbarButton(clearBtn, Color.decode("#7A8C8F"));
        clearBtn.addActionListener(e -> {
            dataReference.clear();
            refreshTable();
        });

        buttonPanel.add(addBtn);
        buttonPanel.add(clearBtn);
        top.add(title, BorderLayout.WEST);
        top.add(buttonPanel, BorderLayout.EAST);
        
        return top;
    }

    private void styleToolbarButton(RoundButton btn, Color foreground) {
        btn.setBackground(Color.decode("#122529"));
        btn.setForeground(foreground);
        btn.setPreferredSize(new Dimension(90, 30));
        btn.setFont(new Font("SansSerif", Font.BOLD, 10));
    }

    private void setupTable() {
        model = new DefaultTableModel(new String[]{"#", "Value Input"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 1; }
        };

        table = new JTable(model);
        table.setBackground(BACKGROUND);
        table.setForeground(TEXT_MAIN);
        table.setGridColor(GRID_COLOR);
        table.setRowHeight(35);
        table.setSelectionBackground(Color.decode("#162C30"));
        table.setSelectionForeground(Color.decode("#44cee6"));
        table.setFont(new Font("Monospaced", Font.PLAIN, 13));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        table.getTableHeader().setBackground(HEADER_BG);
        table.getTableHeader().setForeground(ACCENT);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));

        table.getModel().addTableModelListener(e -> {
            if (!isRefreshing && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                if (row >= 0 && row < dataReference.size()) {
                    try {
                        Object valObj = model.getValueAt(row, 1);
                        double val = Double.parseDouble(valObj.toString());
                        dataReference.set(row, val);
                    } catch (Exception ignored) {}
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = table.rowAtPoint(e.getPoint());
                if (r >= 0 && r < table.getRowCount()) {
                    table.setRowSelectionInterval(r, r);
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu menu = new JPopupMenu();
                    menu.setBorder(BorderFactory.createLineBorder(GRID_COLOR));
                    JMenuItem remove = new JMenuItem("Remove Row " + (r + 1));
                    remove.addActionListener(al -> {
                        if (r != -1 && r < dataReference.size()) {
                            dataReference.remove(r);
                            refreshTable();
                        }
                    });
                    menu.add(remove);
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public void refreshTable() {
        isRefreshing = true;
        model.setRowCount(0);
        for (int i = 0; i < dataReference.size(); i++) {
            model.addRow(new Object[]{i + 1, dataReference.get(i)});
        }
        isRefreshing = false;
        revalidate();
        repaint();
    }
}