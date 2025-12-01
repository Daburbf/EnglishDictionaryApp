package main.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExplosionEffect {
    private static Timer animationTimer;
    
    public static void triggerExplosion(Window window) {
        createExplosion(window);
    }
    
    private static void createExplosion(Window window) {
        JDialog dialog = new JDialog(window, "", Dialog.ModalityType.MODELESS);
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);
        dialog.setBackground(new Color(0, 0, 0, 0));
        
        Dimension windowSize = window.getSize();
        dialog.setSize(windowSize);
        dialog.setLocation(window.getLocation());
        
        ExplosionPanel explosionPanel = new ExplosionPanel(windowSize);
        dialog.add(explosionPanel);
        dialog.setVisible(true);
        
        animationTimer = new Timer(16, e -> {
            explosionPanel.repaint();
        });
        animationTimer.start();
        
        Timer closeTimer = new Timer(6000, e -> {
            dialog.dispose();
            animationTimer.stop();
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
        
        explosionPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                dialog.dispose();
                animationTimer.stop();
                closeTimer.stop();
            }
        });
    }
    
    static class ExplosionPanel extends JPanel {
        private List<Particle> particles = new ArrayList<>();
        private Dimension panelSize;
        private long startTime;
        private Random random = new Random();
        private long lastExplosionTime = 0;
        private int explosionCount = 0;
        
        public ExplosionPanel(Dimension size) {
            setOpaque(false);
            this.panelSize = size;
            setPreferredSize(size);
            this.startTime = System.currentTimeMillis();
            this.lastExplosionTime = startTime;
        }
        
        private void createExplosionAt(float x, float y) {
            int particleCount = 60 + random.nextInt(40);
            
            for (int i = 0; i < particleCount; i++) {
                particles.add(new Particle(x, y));
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            long currentTime = System.currentTimeMillis();
            
            if (currentTime - lastExplosionTime > 300 && explosionCount < 8) {
                createRandomExplosion();
                lastExplosionTime = currentTime;
                explosionCount++;
            }
            
            for (int i = particles.size() - 1; i >= 0; i--) {
                Particle p = particles.get(i);
                if (p.life > 0) {
                    p.update();
                    p.draw(g2d);
                } else {
                    particles.remove(i);
                }
            }
        }
        
        private void createRandomExplosion() {
            int width = panelSize.width;
            int height = panelSize.height;
            
            if (width <= 0) width = 800;
            if (height <= 0) height = 600;
            
            float x = random.nextInt(width - 100) + 50;
            float y = random.nextInt(height - 100) + 50;
            
            createExplosionAt(x, y);
        }
    }
    
    static class Particle {
        float x, y, vx, vy, size, life = 1.0f;
        Color color;
        Random random = new Random();
        float originalSize;
        
        Particle(float startX, float startY) {
            this.x = startX;
            this.y = startY;
            
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = random.nextDouble() * 8 + 4;
            
            vx = (float) (Math.cos(angle) * speed);
            vy = (float) (Math.sin(angle) * speed);
            
            originalSize = random.nextFloat() * 10 + 6;
            size = originalSize;
            
            int colorType = random.nextInt(8);
            switch (colorType) {
                case 0: color = new Color(255, 100, 50);
                case 1: color = new Color(255, 200, 50);
                case 2: color = new Color(255, 255, 100);
                case 3: color = new Color(255, 150, 100);
                case 4: color = new Color(255, 100, 100);
                case 5: color = new Color(255, 200, 100);
                case 6: color = new Color(255, 150, 50);
                case 7: color = new Color(255, 100, 150);
            }
        }
        
        void update() {
            x += vx;
            y += vy;
            vy += 0.08f;
            vx *= 0.995f;
            vy *= 0.995f;
            size = originalSize * life;
            life -= 0.004f;
        }
        
        void draw(Graphics2D g2d) {
            if (life > 0) {
                GradientPaint gradient = new GradientPaint(
                    x, y, new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(life * 255)),
                    x + size, y + size, new Color(255, 255, 200, (int)(life * 150))
                );
                g2d.setPaint(gradient);
                
                Ellipse2D.Float particleShape = new Ellipse2D.Float(x, y, size, size);
                g2d.fill(particleShape);

                g2d.setColor(new Color(255, 255, 200, (int)(life * 100)));
                g2d.fill(new Ellipse2D.Float(x - 2, y - 2, size + 4, size + 4));
                
                if (random.nextFloat() > 0.8f && life > 0.3f) {
                    g2d.setColor(new Color(255, 255, 255, (int)(life * 200)));
                    int sparkleSize = (int)(size / 2);
                    g2d.fill(new Ellipse2D.Float(
                        x + random.nextInt((int)size - sparkleSize), 
                        y + random.nextInt((int)size - sparkleSize), 
                        sparkleSize, sparkleSize
                    ));
                }
                
                if (life < 0.6f && random.nextFloat() > 0.4f) {
                    g2d.setColor(new Color(80, 80, 80, (int)(life * 120)));
                    float smokeSize = size * 0.8f;
                    g2d.fill(new Ellipse2D.Float(
                        x - vx * 0.8f, 
                        y - vy * 0.8f, 
                        smokeSize, smokeSize
                    ));
                }
            }
        }
    }
}