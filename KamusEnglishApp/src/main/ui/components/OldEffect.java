package main.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class OldEffect {
    
    private static Timer timer;
    private static Map<Component, Color[]> componentColors = new HashMap<>();
    private static String originalTitle;
    
    public static void triggerOldEffect(JFrame mainFrame) {

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        originalTitle = mainFrame.getTitle();
        
        applySimpleOldEffect(mainFrame);

        timer = new Timer(10000, e -> {
            restoreSimpleEffect(mainFrame);
            mainFrame.setTitle(originalTitle);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private static void applySimpleOldEffect(JFrame frame) {

        frame.setTitle(originalTitle + " [OLD MODE]");

        applyToContainer(frame.getContentPane());
    }
    
    private static void applyToContainer(Container container) {
        for (Component comp : container.getComponents()) {

            componentColors.put(comp, new Color[]{
                comp.getBackground(),
                comp.getForeground()
            });

            if (comp instanceof AbstractButton) {
                comp.setBackground(new Color(192, 192, 192));
                comp.setForeground(Color.BLACK);
            } else if (comp instanceof JPanel) {
                comp.setBackground(new Color(212, 208, 200));
            } else if (comp instanceof JTextField || comp instanceof JTextArea) {
                comp.setBackground(Color.WHITE);
                comp.setForeground(Color.BLACK);
            }

            if (comp instanceof Container) {
                applyToContainer((Container) comp);
            }
        }
    }
    
    private static void restoreSimpleEffect(JFrame frame) {
        restoreContainer(frame.getContentPane());
        componentColors.clear();
    }
    
    private static void restoreContainer(Container container) {
        for (Component comp : container.getComponents()) {
            Color[] colors = componentColors.get(comp);
            if (colors != null) {
                comp.setBackground(colors[0]);
                comp.setForeground(colors[1]);
            }
            
            if (comp instanceof Container) {
                restoreContainer((Container) comp);
            }
        }
    }
}