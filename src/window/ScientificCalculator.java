package window;
import javax.swing.*;
import java.awt.*;

public class ScientificCalculator extends JFrame {
    
    public ScientificCalculator(String imagePath) {
        setTitle("ScientificCalc Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setSize(1000, 700);
        
        setLayout(new BorderLayout());

        if (imagePath != null) {
            ImageIcon image = new ImageIcon(imagePath);
            setIconImage(image.getImage());
        }

        // add(new Sidebar(), BorderLayout.WEST);
        // add(new GraphCanvas(), BorderLayout.CENTER);

        setVisible(true);
    }
}