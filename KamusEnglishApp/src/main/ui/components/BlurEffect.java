package main.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class BlurEffect {
    private static Timer blurTimer;
    
    public static void showBlurEffect(Window window) {
        if (blurTimer != null && blurTimer.isRunning()) {
            blurTimer.stop();
        }
        
        try {

            Robot robot = new Robot();
            Point windowLoc = window.getLocationOnScreen();
            Rectangle captureRect = new Rectangle(windowLoc.x, windowLoc.y, window.getWidth(), window.getHeight());
            BufferedImage screenCapture = robot.createScreenCapture(captureRect);

            BufferedImage blurredImage = applyRealGaussianBlur(screenCapture, 15);
            
            createBlurOverlay(window, blurredImage);
            
        } catch (Exception e) {

            System.out.println("Screen capture failed, using fallback blur: " + e.getMessage());
            createFallbackBlurOverlay(window);
        }
    }
    
    private static BufferedImage applyRealGaussianBlur(BufferedImage image, int radius) {
        int size = radius * 2 + 1;
        float[] kernelData = new float[size * size];
        float sigma = radius / 3.0f;
        float sigma2 = 2 * sigma * sigma;
        float sqrtPiSigma2 = (float) (Math.sqrt(2 * Math.PI) * sigma);
        float total = 0;

        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                float value = (float) (Math.exp(-(x * x + y * y) / sigma2) / sqrtPiSigma2);
                kernelData[(y + radius) * size + (x + radius)] = value;
                total += value;
            }
        }
        
        for (int i = 0; i < kernelData.length; i++) {
            kernelData[i] /= total;
        }

        Kernel kernel = new Kernel(size, size, kernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }
    
    private static void createBlurOverlay(Window window, BufferedImage blurredBackground) {
        JDialog blurOverlay = new JDialog(window);
        blurOverlay.setUndecorated(true);
        blurOverlay.setModal(false);
        blurOverlay.setAlwaysOnTop(true);
        blurOverlay.setSize(window.getSize());
        blurOverlay.setLocation(window.getLocation());
        
        JPanel blurPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                if (blurredBackground != null) {
                    g2d.drawImage(blurredBackground, 0, 0, getWidth(), getHeight(), null);
                }

                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                addCameraUI(g2d, getWidth(), getHeight());
            }
        };
        
        blurOverlay.setContentPane(blurPanel);
        blurOverlay.setVisible(true);

        blurTimer = new Timer(5000, e -> {
            blurOverlay.dispose();
            if (blurTimer != null) {
                blurTimer.stop();
            }
        });
        blurTimer.setRepeats(false);
        blurTimer.start();

        blurPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                blurOverlay.dispose();
                if (blurTimer != null) {
                    blurTimer.stop();
                }
            }
        });
    }
    
    private static void createFallbackBlurOverlay(Window window) {
        JDialog blurOverlay = new JDialog(window);
        blurOverlay.setUndecorated(true);
        blurOverlay.setModal(false);
        blurOverlay.setAlwaysOnTop(true);
        blurOverlay.setSize(window.getSize());
        blurOverlay.setLocation(window.getLocation());
        blurOverlay.setBackground(new Color(0, 0, 0, 0));
        
        JPanel blurPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 0; i < 8; i++) {
                    g2d.setColor(new Color(255, 255, 255, 10));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    
                    g2d.setColor(new Color(0, 0, 0, 15));
                    g2d.fillRect(i * 3, i * 3, getWidth() - (i * 6), getHeight() - (i * 6));
                }
                
                addCameraUI(g2d, getWidth(), getHeight());
            }
        };
        
        blurPanel.setOpaque(false);
        blurOverlay.setContentPane(blurPanel);
        blurOverlay.setVisible(true);
        
        blurTimer = new Timer(5000, e -> {
            blurOverlay.dispose();
            if (blurTimer != null) {
                blurTimer.stop();
            }
        });
        blurTimer.setRepeats(false);
        blurTimer.start();
        
        blurPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                blurOverlay.dispose();
                if (blurTimer != null) {
                    blurTimer.stop();
                }
            }
        });
    }
    
    private static void addCameraUI(Graphics2D g2d, int width, int height) {

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2f));
        
        int bracketSize = 80;
        int centerX = width / 2;
        int centerY = height / 2;

        drawFocusBracket(g2d, centerX - bracketSize/2, centerY - bracketSize/2, bracketSize, bracketSize);

        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        String text = "FOCUS LOST";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = centerX - fm.stringWidth(text) / 2;
        int textY = centerY + bracketSize + 40;
        g2d.drawString(text, textX, textY);

        g2d.setColor(new Color(255, 255, 255, 100));
        for (int i = 0; i < 6; i++) {
            int apertureSize = 20 + i * 8;
            g2d.drawOval(centerX - apertureSize/2, centerY - apertureSize/2, apertureSize, apertureSize);
        }
    }
    
    private static void drawFocusBracket(Graphics2D g2d, int x, int y, int width, int height) {
        int bracketLength = 15;

        g2d.drawLine(x, y, x + bracketLength, y);
        g2d.drawLine(x, y, x, y + bracketLength);

        g2d.drawLine(x + width - bracketLength, y, x + width, y);
        g2d.drawLine(x + width, y, x + width, y + bracketLength);

        g2d.drawLine(x, y + height - bracketLength, x, y + height);
        g2d.drawLine(x, y + height, x + bracketLength, y + height);

        g2d.drawLine(x + width - bracketLength, y + height, x + width, y + height);
        g2d.drawLine(x + width, y + height - bracketLength, x + width, y + height);

        g2d.fillOval(x + width/2 - 2, y + height/2 - 2, 4, 4);
    }
}