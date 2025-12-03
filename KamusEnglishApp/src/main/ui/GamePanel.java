package main.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GamePanel extends JPanel {
    private Timer flipTimer;
    private int flipFrames = 0;
    private final int TOTAL_FLIP_FRAMES = 30;
    private boolean isFlipping = false;
    private boolean finalResultHeads = false;
    private JLabel coinLabel;
    private JLabel resultLabel;
    
    public GamePanel() {
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(18, 18, 18));
        setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel headerLabel = new JLabel("Koin Flip");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(46, 204, 113));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JPanel coinPanel = new JPanel(new BorderLayout());
        coinPanel.setBackground(new Color(18, 18, 18));
        
        coinLabel = new JLabel("🪙", SwingConstants.CENTER);
        coinLabel.setFont(new Font("Segoe UI", Font.BOLD, 100));
        coinLabel.setForeground(new Color(241, 196, 15));
        
        coinPanel.add(coinLabel, BorderLayout.CENTER);

        resultLabel = new JLabel("Klik FLIP untuk memulai!", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        resultLabel.setForeground(Color.YELLOW);
        resultLabel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton flipButton = new JButton("FLIP KOIN");
        flipButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        flipButton.setBackground(new Color(241, 196, 15));
        flipButton.setForeground(Color.WHITE);
        flipButton.setFocusPainted(false);
        flipButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        flipButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton backButton = new JButton("← Kembali ke Kamus");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setBackground(new Color(60, 60, 60));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(18, 18, 18));
        buttonPanel.add(flipButton);
        buttonPanel.add(backButton);

        add(headerLabel, BorderLayout.NORTH);
        add(coinPanel, BorderLayout.CENTER);
        add(resultLabel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.SOUTH);

        flipTimer = new Timer(80, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isFlipping) {
                    flipFrames++;
                    animateCoinFlip();
                    
                    if (flipFrames >= TOTAL_FLIP_FRAMES) {
                        flipTimer.stop();
                        isFlipping = false;
                        showFinalResult();
                    }
                }
            }
        });

        flipButton.addActionListener(e -> startCoinFlip());
        backButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame) {
                ((MainFrame) window).showDashboard();
            }
        });
    }

    private void startCoinFlip() {
        if (isFlipping) return;
        
        isFlipping = true;
        flipFrames = 0;

        Random random = new Random();
        finalResultHeads = random.nextBoolean();
        
        resultLabel.setText("Koin sedang berputar...");
        resultLabel.setForeground(Color.CYAN);
        
        flipTimer.start();
    }

    private void animateCoinFlip() {

        String[] coinFrames = {
            "🪙", "⚪", "🟡", "🔄", "⭕", "🔴", 
            "🟠", "🟡", "🟢", "🔵", "🟣", "⚫"
        };
        
        int frameIndex = flipFrames % coinFrames.length;
        coinLabel.setText(coinFrames[frameIndex]);
 
        double progress = (double) flipFrames / TOTAL_FLIP_FRAMES;
        double scale = 1.0 + Math.sin(progress * Math.PI * 2) * 0.2;

        int baseSize = 100;
        int animatedSize = (int) (baseSize * scale);
        coinLabel.setFont(new Font("Segoe UI", Font.BOLD, animatedSize));

        int r = (int) (200 + 55 * Math.sin(progress * Math.PI));
        int g = (int) (180 + 75 * Math.sin(progress * Math.PI + 2));
        int b = (int) (50 + 50 * Math.sin(progress * Math.PI + 4));
        coinLabel.setForeground(new Color(r, g, b));
    }

    private void showFinalResult() {

        if (finalResultHeads) {
            coinLabel.setText("🪙");
            resultLabel.setText("HEADS! (GAMBAR) - Anda Menang!");
            resultLabel.setForeground(new Color(46, 204, 113));
        } else {
            coinLabel.setText("🪙");
            resultLabel.setText("TAILS! (ANGKA) - Anda Menang!");
            resultLabel.setForeground(new Color(52, 152, 219));
        }
        
        coinLabel.setFont(new Font("Segoe UI", Font.BOLD, 100));
        coinLabel.setForeground(new Color(241, 196, 15));
    }
}