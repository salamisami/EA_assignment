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
package games.homeship.robomazeblast;

import games.homeship.robomazeblast.client.ClientThread;
import games.homeship.robomazeblast.client.io.Resource;
import games.homeship.robomazeblast.server.ServerThread;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

/**
 * Main entry point of both Server and Client.
 *
 * @author Christian Lins
 */
public class Main {

    public static final String FONT0_RESOURCE = "res/gfx/fonts/slkscr.ttf";
    public static final String FONT1_RESOURCE = "res/gfx/fonts/slkscrb.ttf";
    public static final String TITLE = "Robo Maze Blast";
    public static final String VERSION = "3.0.0";

    public static boolean Debugging = false;

    public static void main(String[] args) throws Exception {

        // Initialize things
        loadCustomFont();

        // Should we show the Server GUI?
        boolean headlessServer = true;

        // Should we start a Server?
        boolean startServer = true;

        // Should we start a Client?
        boolean startClient = true;

        if (startClient) {
            ClientThread.getInstance().start();
        }

        if (startServer) {
            ServerThread serverThread = new ServerThread(!headlessServer);
            serverThread.start();

            synchronized (serverThread) {
                serverThread.wait();
            }
        }
    }

    private static void loadCustomFont() {
        try {
            GraphicsEnvironment ge
                    = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Resource.getAsStream(FONT0_RESOURCE)));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    Resource.getAsStream(FONT1_RESOURCE)));
        } catch (IOException | FontFormatException ex) {
            ex.printStackTrace();
        }
    }

    public static void startSingleGame() throws IOException {
        final var ct = ClientThread.getInstance();

        ct.connect("localhost");
        ct.getServer().login1("_localuser_");
    }

}
