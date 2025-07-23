/*
 *  Robo Maze Blast
 *  Copyright (C) 2008-2013 Christian Lins <christian@lins.me>
 *  Copyright (C) 2008 Kai Ritterbusch <kai.ritterbusch@googlemail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package games.homeship.robomazeblast.client.gui;

import games.homeship.robomazeblast.client.io.Resource;
import games.homeship.robomazeblast.server.api.Element;
import games.homeship.robomazeblast.server.api.Explodable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JComponent;

/**
 * Paints an Element on a PlaygroundPanel.
 *
 * @author Kai Ritterbusch
 * @author Christian Lins
 */
@SuppressWarnings("serial")
public class ElementPainter extends JComponent {

    public static final Color  BACKGROUND_COLOR = new Color(89, 82, 124);
    public static final int    DEFAULT_SIZE     = 32;
    public static final String EXPLOSION_IMAGE  = "res/gfx/explosion/expl";
    public static final String PLAYER_DIE_IMAGE = "res/gfx/player";

    private static final Map<String, Image> ImageCache;

    /**
     * Loads Images from filesystem and puts them into cache
     */
    static {
        ImageCache = new ConcurrentHashMap<>();

        // Explosion-Image
        for (int n = 1; n <= 5; n++) {
            Image img = Resource.getImage(EXPLOSION_IMAGE + n + ".png")
                    .getImage();
            ImageCache.put(EXPLOSION_IMAGE + n + ".png", img);
        }

        // Player-Die-Image
        for (int n = 1; n <= 4; n++) {
            for (int i = 21; i <= 25; i++) {
                Image img = Resource.getImage(
                        PLAYER_DIE_IMAGE + n + "/" + i + ".png").getImage();
                ImageCache.put(PLAYER_DIE_IMAGE + n + "/" + i + ".png", img);
            }
        }
    }

    private final Image[]   images        = new Image[5];
    private int             explStage     = 0;
    private int             dieStage      = 0;
    private ExplosionTimer  explTimer;
    private DieTimer        dieTimer;
    private Element[]       elements      = new Element[5];
    private int             playerNumber;

    public ElementPainter() {
    }

    /**
     * Stops ExplosionTime -- ExplosionTimer.java
     */
    private void stopExplosionTimer() {
        this.explTimer.cancel();
        this.explTimer = null;
        explStage = 0;
    }

    /**
     * Creates new Explosion Timer
     *
     * @param delay
     * @param period
     */
    synchronized void newExplosion(int delay, int period) {
        if (this.explTimer != null) {
            stopExplosionTimer();
        }

        this.explTimer = new ExplosionTimer(this, delay, period);
    }

    /**
     * Creates new die Timer
     *
     * @param delay
     * @param period
     * @param playerNumber
     */
    synchronized void newDieAnimation(int delay, int period, int playerNumber) {
        if (this.dieTimer != null)
            stopDieTimer();
        this.playerNumber = playerNumber;
        this.dieTimer = new DieTimer(this, delay, period);
    }

    /**
     * Stops die Timer
     */
    private void stopDieTimer() {
        this.dieTimer.cancel();
        this.dieTimer = null;
        explStage = 0;
    }

    /**
     * Switch to next die Image Stops Timer if last Image was shown
     */
    void nextDieImage() {
        dieStage++;
        if (dieStage > 5)
            stopDieTimer();
    }

    /**
     * Switch to next Explosion Image Stops Timer if last Image was shown
     */
    void nextExplosionImage() {
        explStage++;
        if (explStage > 5)
            stopExplosionTimer();
    }

    /**
     * Paint the Image
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D gx = (Graphics2D)g;
        gx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        gx.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        gx.setColor(BACKGROUND_COLOR);
        gx.fillRect(0, 0, getWidth(), getHeight());

        for (Image img : images) {
            if (img != null) {
                g.drawImage(img, 0, 0, DEFAULT_SIZE, DEFAULT_SIZE, null);
            }
        }

        // Draw explosion if one has occurred
        if (explStage > 0 && (elements[0] == null || (elements[0] instanceof Explodable))) {
            Image img = ImageCache.get(EXPLOSION_IMAGE + (explStage + 1)
                    + ".png");
            g.drawImage(img, 0, 0, DEFAULT_SIZE, DEFAULT_SIZE, null);
        }
        // Draw die animation
        if (dieStage > 0) {
            Image img = ImageCache.get(PLAYER_DIE_IMAGE + this.playerNumber
                    + "/" + (dieStage + 20) + ".png");
            g.drawImage(img, 0, 0, DEFAULT_SIZE, DEFAULT_SIZE, null);
        }
    }

    /**
     * Get preferredSize of the Image (e.g. 40px x 40px)
     *
     * @return
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /**
     * Minimum size equals 40px x 40px
     *
     * @return
     */
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    /**
     * Sets Elements of the ElementPainter element[0] equals this.element
     * position of the bombs
     *
     * @param elements
     */
    public void setElements(Element[] elements) {
        if (elements == null || elements.length < 1) {
            throw new IllegalArgumentException("elements must not be null or length = 0");
        }
        this.elements = elements;

        for (int n = 0; n < elements.length; n++) {
            if (elements[n] == null) {
                this.images[n] = null;
            } else {
                String imageFilename = elements[n].getImageFilename();
                images[n] = ImageCache.get(imageFilename);
                if (images[n] == null) {
                    images[n] = Resource.getImage(imageFilename).getImage();
                    ImageCache.put(imageFilename, images[n]);
                }
            }
        }
    }

    public Element[] getElements() {
        return elements;
    }

    @Override
    public int getWidth() {
        return DEFAULT_SIZE;
    }

    @Override
    public int getHeight() {
        return DEFAULT_SIZE;
    }
}
