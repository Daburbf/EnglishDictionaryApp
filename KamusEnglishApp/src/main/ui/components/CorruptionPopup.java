package main.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class CorruptionPopup extends JWindow {
    private JLabel imageLabel;
    
    public CorruptionPopup(Window owner) {
        super(owner);
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setAlwaysOnTop(true);

        setBackground(new Color(0, 0, 0, 0));

        ImageIcon originalIcon = createBahlilImage();
        
        imageLabel = new JLabel(originalIcon);
        imageLabel.setOpaque(false);
        
        add(imageLabel, BorderLayout.CENTER);

        pack();

        makeDraggable();
    }
    
    private ImageIcon createBahlilImage() {
        try {

            String imagePath = "C:\\Users\\TUF GAMING\\Downloads\\bahlil.png.png";
            File file = new File(imagePath);
            
            System.out.println("Loading Bahlil image from: " + imagePath);
            System.out.println("File exists: " + file.exists());
            
            if (file.exists()) {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                Image image = originalIcon.getImage();

                Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } else {
                System.out.println("File not found, using default image");
                return createDefaultImage();
            }
            
        } catch (Exception e) {
            System.out.println("Error loading Bahlil image: " + e.getMessage());
            return createDefaultImage();
        }
    }
    
    private ImageIcon createDefaultImage() {

        int width = 150;
        int height = 150;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(0, 0, 0, 0));
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(new Color(255, 200, 150)); 
        g2d.fillOval(25, 25, 100, 100);
        g2d.setColor(Color.BLACK);
        g2d.fillOval(45, 55, 15, 15);
        g2d.fillOval(90, 55, 15, 15);
        g2d.setColor(Color.RED);
        g2d.fillArc(60, 80, 30, 20, 0, -180);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("BAHLIL", 55, 120);
        g2d.dispose();
        
        return new ImageIcon(image);
    }
    
    private void makeDraggable() {
        MouseAdapter ma = new MouseAdapter() {
            private Point initialClick;
            
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {

                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        };
        
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }
    
    public void followCursor(int x, int y) {
        setLocation(x + 20, y + 20); 
    }
    
    public void showPopup() {
        setVisible(true);
    }
    
    public void hidePopup() {
        setVisible(false);
    }
}