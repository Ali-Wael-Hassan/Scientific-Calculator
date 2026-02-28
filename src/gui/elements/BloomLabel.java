package gui.elements;

import javax.swing.*;
import java.awt.*;

public class BloomLabel extends JLabel {
    private int glowIntensity = 7;

    public BloomLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        Color neonCyan = new Color(0, 229, 255); 
        
        setForeground(neonCyan);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int textWidth = fm.stringWidth(text);
        
        int x;
        int alignment = getHorizontalAlignment();
        if (alignment == RIGHT) {
            x = getWidth() - getInsets().right - textWidth;
        } else if (alignment == CENTER) {
            x = (getWidth() - textWidth) / 2;
        } else {
            x = getInsets().left;
        }

        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

        Color baseColor = getForeground();
        for (int i = glowIntensity; i > 0; i--) {
            float alpha = 0.1f / i;
            g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), (int)(alpha * 255)));
            
            g2.drawString(getText(), x - i, y);
            g2.drawString(getText(), x + i, y);
            g2.drawString(getText(), x, y - i);
            g2.drawString(getText(), x, y + i);
        }

        g2.setColor(baseColor);
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}