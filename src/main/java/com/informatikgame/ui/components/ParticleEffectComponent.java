package com.informatikgame.ui.components;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * Component that renders particle effects (Matrix rain) - preserves original
 * particle logic
 */
public class ParticleEffectComponent extends AnimatedComponent {

    private Particle[] particles;
    private final int particleCount;

    public ParticleEffectComponent(int particleCount) {
        super();
        this.particleCount = particleCount;
        initializeParticles();
    }

    public ParticleEffectComponent() {
        this(30); // Default particle count
    }

    private void initializeParticles() {
        particles = new Particle[particleCount];
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle();
        }
    }

    @Override
    protected void renderAnimation(TextGraphics graphics, TerminalSize size) {
        // Update particles
        for (Particle p : particles) {
            p.update(size);
        }

        // Render particles (exact same logic as original)
        for (Particle p : particles) {
            if (p.x < size.getColumns() && p.y < size.getRows()) {
                graphics.setForegroundColor(p.color);
                graphics.setCharacter(p.x, p.y, p.symbol);
            }
        }
    }

    @Override
    public void updateAnimation() {
        super.updateAnimation();
        // Additional particle updates if needed
    }

    /**
     * Particle class - exact copy from original MainMenuScreen
     */
    private class Particle {

        int x, y;
        char symbol;
        TextColor color;
        int speed;

        Particle() {
            reset(getSize());
        }

        void reset(TerminalSize size) {
            if (size.getColumns() > 0 && size.getRows() > 0) {
                x = (int) (Math.random() * size.getColumns());
                y = 0;
                speed = 1 + (int) (Math.random() * 3);

                // Verschiedene Symbole für Atmosphäre (same as original)
                char[] symbols = {'░', '▒', '▓', '█', '*', '·', '•'};
                symbol = symbols[(int) (Math.random() * symbols.length)];

                // Grün-Töne für Matrix-Effekt (same as original)
                int green = 50 + (int) (Math.random() * 150);
                color = new TextColor.RGB(0, green, 0);
            }
        }

        void update(TerminalSize size) {
            y += speed;
            if (y >= size.getRows()) {
                reset(size);
            }
        }
    }
}
