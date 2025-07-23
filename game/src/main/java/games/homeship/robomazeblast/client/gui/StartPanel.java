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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Shows the splash screen and the login window.
 *
 * @author Kai Ritterbusch
 * @author Christian Lins
 */
public class StartPanel extends JPanel implements KeyListener {

    private final MenuPanel menuPanel = new MenuPanel();
    private final Font      titleFont = new Font("Silkscreen", Font.PLAIN, 74);

    public StartPanel() {
        setBackground(Color.BLACK);
        setLayout(null);
        add(menuPanel);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                int x = getWidth() / 2 - 200;
                int y = getHeight() - 350;
                menuPanel.setBounds(x, y, 400, 230);
            }
        });

        var repaintAction = (ActionListener) (ActionEvent e) -> {
            repaint();
        };
        new Timer(1000, repaintAction).start();
    }

    /**
     * Shows the Background for the Panel
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Make it black

        Random rnd = new Random();
        g.setColor(ElementPainter.BACKGROUND_COLOR);
        for (int x = 0; x < getWidth(); x++) {
            float r0 = Math.abs(getWidth() / 2.0f - x) / getWidth() / 2.0f;
            for (int y = 0; y < getHeight(); y+=rnd.nextInt(3)) {
                float r1 = rnd.nextFloat() / 4;
                if (r0 < r1) {
                    int s = rnd.nextInt(5);
                    g.fillRect(x, y, s, s);
                }
            }
        }

        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        g.drawString("Robo Maze Blast", getWidth() / 2 - 370, 200);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        menuPanel.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
