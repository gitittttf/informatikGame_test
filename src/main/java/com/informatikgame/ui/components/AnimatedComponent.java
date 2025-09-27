package com.informatikgame.ui.components;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.AbstractComponent;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * Base class for animated components that preserves all original animation
 * logic
 */
public abstract class AnimatedComponent extends AbstractComponent<AnimatedComponent> {

    protected int animationFrame = 0;
    protected long lastUpdateTime = System.currentTimeMillis();

    public AnimatedComponent() {
        super();
    }

    /**
     * Update animation frame - called by the GUI system
     */
    public void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > 100) { // Alle 100ms - same as original
            animationFrame++;
            lastUpdateTime = currentTime;
            invalidate(); // Trigger redraw
        }
    }

    @Override
    protected ComponentRenderer<AnimatedComponent> createDefaultRenderer() {
        return new AnimatedComponentRenderer();
    }

    /**
     * Override this to implement your custom animated drawing
     */
    protected abstract void renderAnimation(TextGraphics graphics, TerminalSize size);

    /**
     * Helper method to draw centered text (preserved from original)
     */
    protected void drawCentered(TextGraphics graphics, String text, int y) {
        TerminalSize size = getSize();
        int x = (size.getColumns() - text.length()) / 2;
        graphics.putString(new TerminalPosition(Math.max(0, x), y), text);
    }

    /**
     * Helper method to draw boxes (preserved from original)
     */
    protected void drawBox(TextGraphics graphics, int x, int y, int width, int height,
            TextColor borderColor, TextColor backgroundColor) {
        graphics.setBackgroundColor(backgroundColor);
        graphics.setForegroundColor(borderColor);

        // Draw corners and borders
        graphics.setCharacter(x, y, '╔');
        graphics.setCharacter(x + width - 1, y, '╗');
        graphics.setCharacter(x, y + height - 1, '╚');
        graphics.setCharacter(x + width - 1, y + height - 1, '╝');

        // Draw horizontal lines
        for (int i = x + 1; i < x + width - 1; i++) {
            graphics.setCharacter(i, y, '═');
            graphics.setCharacter(i, y + height - 1, '═');
        }

        // Draw vertical lines
        for (int i = y + 1; i < y + height - 1; i++) {
            graphics.setCharacter(x, i, '║');
            graphics.setCharacter(x + width - 1, i, '║');
        }

        // Fill interior
        graphics.setBackgroundColor(backgroundColor);
        for (int py = y + 1; py < y + height - 1; py++) {
            for (int px = x + 1; px < x + width - 1; px++) {
                graphics.setCharacter(px, py, ' ');
            }
        }
    }

    private class AnimatedComponentRenderer implements ComponentRenderer<AnimatedComponent> {

        @Override
        public TerminalSize getPreferredSize(AnimatedComponent component) {
            return new TerminalSize(80, 25); // Default size
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, AnimatedComponent component) {
            TerminalSize size = component.getSize();
            component.renderAnimation(graphics, size);
        }
    }
}
