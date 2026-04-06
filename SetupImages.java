import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SetupImages {
    public static void main(String[] args) {
        try {
            createImage("fan.png", "Fan", new Color(0, 100, 0)); // Green
            createImage("cycle.png", "Cycle", new Color(255, 215, 0)); // Yellow
            createImage("glass.png", "Glass", new Color(220, 20, 60)); // Red
            createImage("nota.png", "NOTA", Color.GRAY);
            System.out.println("Images created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createImage(String filename, String symbol, Color color) throws IOException {
        int size = 100;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Transparent background
        // g2.setColor(new Color(0,0,0,0)); 
        // g2.fillRect(0,0,size,size);

        // Draw Symbol
        g2.setColor(color);
        if(symbol.equals("Fan")) {
            g2.fillArc(10, 10, 80, 80, 0, 360);
            g2.setColor(Color.WHITE);
            g2.fillArc(15, 15, 70, 70, 0, 60);
            g2.fillArc(15, 15, 70, 70, 120, 60);
            g2.fillArc(15, 15, 70, 70, 240, 60);
        } else if (symbol.equals("Cycle")) {
           g2.setStroke(new BasicStroke(5));
           g2.drawOval(15, 45, 25, 25); // Wheel 1
           g2.drawOval(55, 45, 25, 25); // Wheel 2
           g2.drawLine(27, 57, 47, 35); // Frame
           g2.drawLine(67, 57, 47, 35); // Frame
           g2.drawLine(47, 35, 52, 35); // Handle
        } else if (symbol.equals("Glass")) {
             g2.fillPolygon(new int[]{25, 75, 65, 35}, new int[]{20, 20, 80, 80}, 4);
        } else {
            g2.fillRect(20, 40, 60, 20);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("X", 42, 58);
        }
        g2.dispose();

        ImageIO.write(image, "png", new File(filename));
    }
}
