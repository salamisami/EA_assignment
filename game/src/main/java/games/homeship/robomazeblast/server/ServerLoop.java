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
package games.homeship.robomazeblast.server;

import games.homeship.robomazeblast.net.Event;
import games.homeship.robomazeblast.server.api.Session;
import java.util.Map.Entry;

/**
 * The Server loop.
 *
 * @author Christian Lins
 * @author Kai Ritterbusch
 */
public class ServerLoop extends Thread {
    private final Server server;
    private boolean pendingUpdate;

    public ServerLoop(Server server) {
        this.server = server;
    }

    public void enablePendingUpdate() {
        pendingUpdate = true;
    }

    /**
     * This method runs in a loop while the associated @see{Server} is running.
     */
    @Override
    public void run() {
        for (;;) {
            // We are now going to handle all updates, so this can be set to
            // false. Later the variable is checked if an update reached us in
            // the meantime.
            pendingUpdate = false;

            try {
                for (Entry<String, Game> entry : server.getGames().entrySet()) {
                    Game game = entry.getValue();
                    synchronized(game) {
                        // Check if there are enough real players or any
                        // spectators left for gaming
                        if (game.isRunning()
                                && (game.getPlayerCount() == 1 ||
                                   (game.getPlayerSessions().isEmpty() &&
                                    game.getSpectatorSessions().isEmpty())))
                        {
                            game.setRunning(false);

                            // Send won game message to the remaining user
                            // (if not AIPlayer)
                            if (!game.getPlayerSessions().isEmpty()) {
                                this.server.getClients()
                                        .get(game.getPlayerSessions().get(0))
                                        .gameStopped(new Event(new Object[] { 2 }));

                                // We have to store the game result in the Highscore
                                // list
                                this.server.getHighscore().hasWonGame(
                                        game.getPlayers().get(0).getNickname());
                            }

                            // Send gameStopped message to spectators if existing
                            if (!game.getSpectatorSessions().isEmpty()) {
                                for (Session sess : game.getSpectatorSessions()) {
                                    this.server
                                            .getClients()
                                            .get(sess)
                                            .gameStopped(
                                                    new Event(new Object[] { 0 }));
                                }
                            }

                            for (Session sess : game.getPlayerSessions())
                                this.server.getPlayerToGame().remove(sess);

                            this.server.getGames().remove(game.toString());
                            this.server.refresh();
                            break; // Stop the for-loop
                        }

                        // Check if it is necessary to send playground update
                        // messages to the Clients
                        if (game.isPlaygroundUpdateRequired()) {
                            // Updates Playground when moved
                            for (Session sess : game.getPlayerSessions()) {
                                this.server
                                        .getClients()
                                        .get(sess)
                                        .playgroundUpdate(
                                                new Event(new Object[] { game
                                                        .getPlayground() }));
                            }
                            // Updates Playground for Spectator when moved
                            for (Session sess : game.getSpectatorSessions()) {
                                this.server
                                        .getClients()
                                        .get(sess)
                                        .playgroundUpdate(
                                                new Event(new Object[] { game
                                                        .getPlayground() }));
                            }
                        }
                    }
                }

                //Thread.sleep(100); // TODO: Little hacky, no real
                                   // producer/consumer
                synchronized(this) {
                    while (!pendingUpdate) {
                        wait();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Game exit.");
                System.exit(-1);
            }
        }
    }
}
