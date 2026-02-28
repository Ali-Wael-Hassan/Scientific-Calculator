package gui.elements;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundButton extends JButton {
    private int round;
    private int bloomLevel = 7;
    private boolean bloomEnabled = false;
    private Color bloomColor = new Color(0, 255, 255);

    public RoundButton(String text, int round) {
        super(text);
        this.round = round;

        setContentAreaFilled(false); 
        setFocusPainted(false);
        setBorderPainted(false);
    }

    public void setBloomEnabled(boolean bloomEnabled) {
        this.bloomEnabled = bloomEnabled;
        repaint();
    }

    public void setBloomLevel(int bloomLevel) {
        this.bloomLevel = bloomLevel;
    }

    public void setBloomColor(Color color) {
        this.bloomColor = color;
        repaint();
    }

    public void setRound(int round) {
        this.round = round;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if(bloomEnabled) {
            bloomDraw(g2);
        }
        normalDraw(g2);

        g2.dispose();
        
        super.paintComponent(g);
    }

    private void bloomDraw(Graphics2D g2) {
        int baseAlpha = 15;
        g2.setColor(new Color(bloomColor.getRed(), bloomColor.getGreen(), bloomColor.getBlue(), baseAlpha));

        for(int i = 0; i < bloomLevel; ++i) {
            int currentPad = i;
            
            int currentWidth = getWidth() - (currentPad * 2);
            int currentHeight = getHeight() - (currentPad * 2);
            
            int currentRound = round + ((bloomLevel - i) * 2); 

            g2.fill(new RoundRectangle2D.Float(currentPad, currentPad, currentWidth, currentHeight, currentRound, currentRound));
        }
    }

    private void normalDraw(Graphics2D g2) {
        Color bg = getBackground();
        if(getModel().isPressed()) {
            bg = bg.darker();
        } else if(getModel().isRollover()) {
            bg = bg.brighter();
        }

        int pad = bloomEnabled ? bloomLevel : 0;
        int coreWidth = getWidth() - (pad * 2);
        int coreHeight = getHeight() - (pad * 2);

        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(pad, pad, coreWidth, coreHeight, round, round));
    }
}
