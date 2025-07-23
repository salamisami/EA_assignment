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

import games.homeship.robomazeblast.client.ClientThread;
import games.homeship.robomazeblast.net.Event;
import games.homeship.robomazeblast.server.Playground;
import games.homeship.robomazeblast.server.api.Element;
import games.homeship.robomazeblast.server.api.ServerInterface;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ImageCapabilities;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.VolatileImage;
import javax.swing.JPanel;

/**
 * Panel that displays a game's playground. The client receives changes from the
 * server and displays this changes on a PlaygroundPanel.
 *
 * @author Kai Ritterbusch
 * @author Christian Lins
 */
@SuppressWarnings("serial")
public class PlaygroundPanel extends JPanel implements KeyListener {

    private final ElementPainter[][] elementPainter;
    private boolean                  spectatorStatus = false;
    private VolatileImage            buffer;
    private Graphics2D               bufferGraphics;
    private final int bufferSizeX;
    private final int bufferSizeY;
    private final int playgroundOffsetX;
    private final int playgroundOffsetY;
    private final ServerInterface    server;

    public PlaygroundPanel(int cols, int rows, boolean spectatorStatus, ServerInterface server) {
        this.server = server;
        setBackground(Color.BLACK);
        this.spectatorStatus = spectatorStatus;

        this.elementPainter = new ElementPainter[cols][rows];

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                this.elementPainter[x][y] = new ElementPainter();
            }
        }

        // We buffer for ourself
        setDoubleBuffered(false);

        bufferSizeX = cols * ElementPainter.DEFAULT_SIZE + 256; // add space for interface left and right
        bufferSizeY = rows * ElementPainter.DEFAULT_SIZE + 8; // add margin top/bottom
        playgroundOffsetX = bufferSizeX / 2 - cols * ElementPainter.DEFAULT_SIZE / 2;
        playgroundOffsetY = bufferSizeY / 2 - rows * ElementPainter.DEFAULT_SIZE / 2;
    }

    /**
     * Draws the die animation
     *
     * @param x
     * @param y
     * @param playerNumber
     */
    public void drawDieAnimation(int x, int y, int playerNumber) {
        int explPeriod = 150;
        this.elementPainter[x][y].newDieAnimation(0, explPeriod, playerNumber);
    }

    /**
     * Draws explosion animation
     *
     * @param x
     * @param y
     * @param distance
     */
    public void drawExplosion(int x, int y, int distance) {
        int explPeriod = ExplosionTimer.EXPLOSION_TIMER_ANIMSPEED;
        this.elementPainter[x][y].newExplosion(0, explPeriod);

        boolean skipxp = false;
        boolean skipxm = false;
        boolean skipyp = false;
        boolean skipym = false;

        for (int i = 1; i <= distance; i++) {
            if (x + i < this.elementPainter.length && !skipxp) {
                this.elementPainter[x + i][y].newExplosion(i * explPeriod, explPeriod);
                if (this.elementPainter[x + i][y].getElements()[0] != null)
                    skipxp = true;
            }
            if (x - i >= 0 && !skipxm) {
                this.elementPainter[x - i][y].newExplosion(i * explPeriod, explPeriod);
                if (this.elementPainter[x - i][y].getElements()[0] != null)
                    skipxm = true;
            }
            if (y + i < this.elementPainter[0].length && !skipyp) {
                this.elementPainter[x][y + i].newExplosion(i * explPeriod, explPeriod);
                if (this.elementPainter[x][y + i].getElements()[0] != null)
                    skipyp = true;
            }
            if (y - i >= 0 && !skipym) {
                this.elementPainter[x][y - i].newExplosion(i * explPeriod, explPeriod);
                if (this.elementPainter[x][y - i].getElements()[0] != null)
                    skipym = true;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyReleased(KeyEvent event) {
    }

    /**
     * Reacts on player actions.
     *
     * @param event
     */
    @Override
    public void keyPressed(KeyEvent event) {
        try {
            /*
             * Do nothing but reacting to ESC if the calling client is a
             * spectator.
             */
            if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                server.leaveGame(new Event(
                        new Object[] { ClientThread.getInstance().Session }));
                ClientThread.getInstance().ServerListener
                        .playerLeftGame(new Event(new Object[] {}));
            }

            if (!spectatorStatus) {
                final var x = 1;
                final var y = 2;
                var moveArgs = new Object[] {
                    ClientThread.getInstance().Session, 0, 0 };

                switch (event.getKeyCode()) {
                    case KeyEvent.VK_W, KeyEvent.VK_UP -> {
                        moveArgs[y] = -1;
                        server.move(new Event(moveArgs));
                    }
                    case KeyEvent.VK_S, KeyEvent.VK_DOWN ->  {
                        moveArgs[y] = +1;
                        server.move(new Event(moveArgs));
                    }
                    case KeyEvent.VK_A, KeyEvent.VK_LEFT -> {
                        moveArgs[x] = -1;
                        server.move(new Event(moveArgs));
                    }
                    case KeyEvent.VK_D, KeyEvent.VK_RIGHT ->  {
                        moveArgs[x] = +1;
                        server.move(new Event(moveArgs));
                    }
                    case KeyEvent.VK_SPACE -> {
                        server.placeBomb(new Event(
                                new Object[] { ClientThread.getInstance().Session }));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        final Graphics2D gx = (Graphics2D)g;
        gx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        gx.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        gx.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        gx.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        super.paintComponent(gx);

        paintBuffer();

        float scale = 1.0f;
        if (bufferSizeX * 1.2 <= getWidth() && bufferSizeY * 1.2 <= getHeight()) {
            scale = 1.2f;
        }
        if (bufferSizeX * 1.5 <= getWidth() && bufferSizeY * 1.5 <= getHeight()) {
            scale = 1.5f;
        }
        if (bufferSizeX * 2 <= getWidth() && bufferSizeY * 2 <= getHeight()) {
            scale = 2;
        }

        gx.scale(scale, scale);
        gx.drawImage(buffer,
                (int)(getWidth() / scale) / 2 - bufferSizeX / 2,
                (int)(getHeight() / scale) / 2 - bufferSizeY / 2,
                null);
    }

    protected void paintBuffer() {
        if (bufferGraphics == null) {
            if (buffer == null) {
                try {
                    buffer = createVolatileImage(bufferSizeX, bufferSizeY, new ImageCapabilities(true));
                } catch (AWTException ex) {
                    ex.printStackTrace();
                }
            }
            bufferGraphics = (Graphics2D)buffer.createGraphics();
        }

        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.fillRect(0, 0, bufferSizeX, bufferSizeY);

        int cols = elementPainter.length;
        int rows = elementPainter[0].length;

        bufferGraphics.translate(playgroundOffsetX, playgroundOffsetY);

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                int offX = x * ElementPainter.DEFAULT_SIZE;
                int offY = y * ElementPainter.DEFAULT_SIZE;
                bufferGraphics.translate(offX, offY);
                elementPainter[x][y].paintComponent(bufferGraphics);
                bufferGraphics.translate(-offX, -offY);
            }
        }

        bufferGraphics.translate(-playgroundOffsetX, -playgroundOffsetY);
    }

    public void updatePlaygroundView(Playground playground) {
        int cols = playground.getColumns();
        int rows = playground.getRows();

        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                Element[] newElements = playground.getElement(x, y);
                elementPainter[x][y].setElements(newElements);
            }
        }

        repaint();
    }

}
