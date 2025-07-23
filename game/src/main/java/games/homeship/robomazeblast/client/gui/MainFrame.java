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

import games.homeship.robomazeblast.Main;
import games.homeship.robomazeblast.client.AudioThread;
import games.homeship.robomazeblast.client.ClientThread;
import games.homeship.robomazeblast.client.io.Resource;
import games.homeship.robomazeblast.net.Event;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import javax.swing.JFrame;

/**
 * The main application window.
 *
 * @author Kai Ritterbusch
 * @author Christian Lins
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {
    private static final MainFrame instance = new MainFrame();

    public static MainFrame getInstance() {
        return instance;
    }

    protected StartPanel startPanel = new StartPanel();

    private MainFrame() {
        setTitle(Main.TITLE);

        showStartPanel();
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent w) {
                try {
                    if (ClientThread.getInstance().Session != null) {
                        System.out.println("Send logout message to server...");
                        ClientThread.getInstance().getServer()
                                .logout(new Event(new Object[] { ClientThread
                                        .getInstance().Session }));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        this.addWindowListener(listener);

        URL sound = Resource.getAsURL("res/sfx/battle.wav");
        if (sound != null) {
            AudioThread.playSound(sound);
        }

        setMinimumSize(new Dimension(1280, 800));
        setPreferredSize(getMinimumSize());
        setSize(1280, 800);
        setLocationRelativeTo(null);
        pack();
        setResizable(true);
    }

    /**
     * Sets the ContentPane of the mainframe
     *
     * @param cnt
     */
    @Override
    public void setContentPane(Container cnt) {
        super.setContentPane(cnt);
        repaint();
    }

    public void showStartPanel() {
        setContentPane(this.startPanel);
    }
}
