package main.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MatrixRainEffect {
    private static Timer animationTimer;
    private static JDialog dialog;

    public static void triggerMatrixRain(Window window) {
        if (window == null) return;
        createMatrixRain(window);
    }

    private static void createMatrixRain(Window window) {
        Point windowLocation = window.getLocationOnScreen();
        Dimension windowSize = window.getSize();

        dialog = new JDialog(window, "", Dialog.ModalityType.MODELESS);
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setFocusableWindowState(false);

        int dialogWidth = windowSize.width;
        int dialogHeight = windowSize.height;
        int dialogX = windowLocation.x;
        int dialogY = windowLocation.y;

        dialog.setBounds(dialogX, dialogY, dialogWidth, dialogHeight);

        MatrixPanel matrixPanel = new MatrixPanel(dialogWidth, dialogHeight);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(matrixPanel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setVisible(true);

        animationTimer = new Timer(50, (ActionEvent e) -> {
            try {
                matrixPanel.updateState();
                matrixPanel.repaint();
            } catch (Exception ex) {

            }
        });
        animationTimer.start();

        Timer closeTimer = new Timer(8000, (ActionEvent e) -> {
            safeStopAnimation();
        });
        closeTimer.setRepeats(false);
        closeTimer.start();

        matrixPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                safeStopAnimation();
                closeTimer.stop();
            }
        });
    }

    private static void safeStopAnimation() {
        try {
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }
        } catch (Exception ignored) {}
        try {
            if (dialog != null && dialog.isDisplayable()) {
                dialog.dispose();
            }
        } catch (Exception ignored) {}
        animationTimer = null;
        dialog = null;
    }

    static class MatrixPanel extends JPanel {
        private final List<MatrixColumn> columns;
        private final Random random;
        private long startTime;
        private float fadeProgress = 0f;
        private boolean fadingIn = true;

        public MatrixPanel(int preferredWidth, int preferredHeight) {
            this.random = new Random();
            this.startTime = System.currentTimeMillis();
            this.columns = new ArrayList<>();

            setOpaque(false);
            setPreferredSize(new Dimension(preferredWidth, preferredHeight));

            int columnCount = Math.max(2, preferredWidth / 20);
            for (int i = 0; i < columnCount; i++) {
                int xPos = i * 20 + random.nextInt(20);
                columns.add(new MatrixColumn(xPos, preferredHeight, random));
            }
        }

        public void updateState() {
            long currentTime = System.currentTimeMillis();
            float elapsed = (currentTime - startTime) / 1000.0f;

            if (fadingIn) {
                fadeProgress = Math.min(1.0f, elapsed / 1.0f);
            } else {

                fadeProgress = Math.max(0f, 1.0f - (elapsed - 6.0f));
            }

            if (elapsed > 6.0f && fadingIn) {
                fadingIn = false;

                startTime = currentTime - 6000;
            }

            int currentHeight = getHeight() > 0 ? getHeight() : getPreferredSize().height;
            for (MatrixColumn column : columns) {
                column.ensureHeight(currentHeight);
                column.update();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            Graphics2D g2d = (Graphics2D) g.create();

            int bgAlpha = Math.max(0, Math.min(180, (int) (180 * fadeProgress)));
            g2d.setColor(new Color(0, 0, 0, bgAlpha));
            g2d.fillRect(0, 0, Math.max(1, w), Math.max(1, h));

            for (MatrixColumn column : columns) {
                column.draw(g2d, fadeProgress);
            }

            int overlayAlpha = Math.max(0, Math.min(20, (int) (20 * fadeProgress)));
            g2d.setColor(new Color(0, 255, 0, overlayAlpha));
            g2d.fillRect(0, 0, Math.max(1, w), Math.max(1, h));

            g2d.dispose();
        }
    }

    static class MatrixColumn {
        private int height;
        private final List<MatrixChar> chars;
        private final Random random;
        private int speed;
        private int charCount;
        private int delayCounter;
        private int maxDelay;

        public MatrixColumn(int x, int height, Random sharedRandom) {
            this.random = sharedRandom != null ? sharedRandom : new Random();
            this.height = Math.max(100, height);
            this.speed = this.random.nextInt(3) + 2;
            this.charCount = Math.max(5, this.height / 20);
            this.chars = new ArrayList<>();
            this.delayCounter = 0;
            this.maxDelay = this.random.nextInt(100);

            int startX = x;
            for (int i = 0; i < charCount; i++) {
                int startY = i * -20;
                chars.add(new MatrixChar(startX, startY, getRandomChar(), this.random));
            }
        }

        public void ensureHeight(int h) {
            if (h > 0) this.height = h;
        }

        public void update() {
            if (delayCounter < maxDelay) {
                delayCounter++;
                return;
            }

            for (int i = 0; i < chars.size(); i++) {
                MatrixChar ch = chars.get(i);
                ch.update(speed);

                if (ch.getY() > height + 20) {
                    ch.setY(-20);
                    ch.setCharacter(getRandomChar());
                    ch.setBrightness(1.0f);
                }

                float distanceFromTop = Math.max(0, ch.getY()) / (float) height;
                ch.setBrightness(Math.max(0f, 1.0f - distanceFromTop * 0.8f));
            }
        }

        public void draw(Graphics2D g2d, float fadeProgress) {
            for (MatrixChar ch : chars) {
                ch.draw(g2d, fadeProgress);
            }
        }

        private char getRandomChar() {
            return random.nextBoolean() ? '1' : '0';
        }
    }

    static class MatrixChar {
        private final int x;
        private int y;
        private char character;
        private float brightness;
        private final Random random;

        public MatrixChar(int x, int y, char character, Random random) {
            this.x = x;
            this.y = y;
            this.character = character;
            this.brightness = 1.0f;
            this.random = random != null ? random : new Random();
        }

        public int getY() {
            return y;
        }

        public void setY(int newY) {
            this.y = newY;
        }

        public void setCharacter(char ch) {
            this.character = ch;
        }

        public void setBrightness(float b) {
            this.brightness = Math.max(0f, Math.min(1f, b));
        }

        public void update(int speed) {
            this.y += speed;
        }

        public void draw(Graphics2D g2d, float fadeProgress) {
            if (fadeProgress <= 0f) return;

            int greenValue = Math.max(0, Math.min(255, (int) (brightness * 255 * fadeProgress)));
            int alphaValue = Math.max(0, Math.min(255, (int) (brightness * 255 * fadeProgress)));

            g2d.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
            g2d.setColor(new Color(0, greenValue, 0, alphaValue));
            g2d.drawString(String.valueOf(character), x, y);

            if (brightness > 0.7f) {
                g2d.setColor(new Color(200, 255, 200, Math.max(0, (int) (50 * brightness * fadeProgress))));
                g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
                g2d.drawString(String.valueOf(character), x - 2, y - 2);
            }

            for (int i = 1; i <= 3; i++) {
                int trailY = y - i * 6;
                if (trailY > 0) {
                    float trailBrightness = brightness * (1.0f - i * 0.3f);
                    int trailGreen = Math.max(0, Math.min(150, (int) (trailBrightness * 150 * fadeProgress)));
                    int trailAlpha = Math.max(0, Math.min(150, (int) (trailBrightness * 150 * fadeProgress)));

                    g2d.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
                    g2d.setColor(new Color(0, trailGreen, 0, trailAlpha));
                    g2d.drawString(String.valueOf(randomTrailChar()), x, trailY);
                }
            }
        }

        private char randomTrailChar() {
            return random.nextBoolean() ? '1' : '0';
        }
    }
}
