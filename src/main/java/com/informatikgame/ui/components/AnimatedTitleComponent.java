package com.informatikgame.ui.components;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.ComponentRenderer;
import com.googlecode.lanterna.gui2.TextGUIGraphics;
import com.informatikgame.ui.ScreenManager;

/**
 * Component for animated title art with glitch effects and typewriter subtitle
 */
public class AnimatedTitleComponent extends AnimatedComponent {

    private final String[] titleArt;
    private final String subtitle;
    private final boolean enableGlitch;
    private final boolean enableTypewriter;

    public AnimatedTitleComponent(String[] titleArt, String subtitle, boolean enableGlitch, boolean enableTypewriter) {
        super();
        this.titleArt = titleArt;
        this.subtitle = subtitle;
        this.enableGlitch = enableGlitch;
        this.enableTypewriter = enableTypewriter;
    }

    @Override
    protected void renderAnimation(TextGraphics graphics, TerminalSize size) {
        // Titel mit Glitch-Effekt (exact same logic as original)
        int titleY = 2;
        for (int i = 0; i < titleArt.length; i++) {
            if (enableGlitch && animationFrame % 30 == 0 && Math.random() < 0.1) {
                // Glitch: verschiebe Zeile leicht (same as original)
                int offset = (int) (Math.random() * 3) - 1;
                graphics.setForegroundColor(new TextColor.RGB(153, 153, 0));
                drawCentered(graphics, titleArt[i], titleY + i + offset);
            } else {
                // Normal: grüne Farbe mit Pulsieren (same as original)
                int brightness = 150 + (int) (Math.sin(animationFrame * 0.2 + i) * 70);
                graphics.setForegroundColor(new TextColor.RGB(0, brightness, 0));
                drawCentered(graphics, titleArt[i], titleY + i);
            }
        }

        // Untertitel mit Typewriter-Effekt (exact same logic as original)
        if (subtitle != null && !subtitle.isEmpty()) {
            graphics.setForegroundColor(ScreenManager.SECONDARY_COLOR);
            String displaySubtitle = subtitle;
            if (enableTypewriter && animationFrame < subtitle.length()) {
                displaySubtitle = subtitle.substring(0, animationFrame);
            }
            drawCentered(graphics, displaySubtitle, titleY + titleArt.length + 2);
        }
    }

    @Override
    protected ComponentRenderer<AnimatedComponent> createDefaultRenderer() {
        return new ComponentRenderer<AnimatedComponent>() {
            @Override
            public TerminalSize getPreferredSize(AnimatedComponent component) {
                // Calculate size based on title art
                int maxWidth = 0;
                for (String line : titleArt) {
                    maxWidth = Math.max(maxWidth, line.length());
                }
                int height = titleArt.length + (subtitle != null ? 4 : 2);
                return new TerminalSize(maxWidth + 4, height);
            }

            @Override
            public void drawComponent(TextGUIGraphics graphics, AnimatedComponent component) {
                TerminalSize size = component.getSize();
                component.renderAnimation(graphics, size);
            }
        };
    }
}
