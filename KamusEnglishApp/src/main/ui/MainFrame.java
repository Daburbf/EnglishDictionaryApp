package main.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DictionaryPanel dictionaryPanel;
    
    public MainFrame() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Lexionary - English Indonesian Dictionary");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(18, 18, 18));

        DashboardPanel dashboardPanel = new DashboardPanel(this);
        dictionaryPanel = new DictionaryPanel(this);
        
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(dictionaryPanel, "DICTIONARY");
        
        add(mainPanel);

        showDashboard();
    }
    
    public void showDashboard() {
        cardLayout.show(mainPanel, "DASHBOARD");
    }
    
    public void showDictionary() {
        cardLayout.show(mainPanel, "DICTIONARY");
    }

    public void removeMouseMotionListeners() {
        for (java.awt.event.MouseMotionListener listener : getMouseMotionListeners()) {
            removeMouseMotionListener(listener);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}