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
package games.homeship.robomazeblast.server.api;

import games.homeship.robomazeblast.net.Event;

/**
 * Main interface for the game server. For easier reflection, every method must
 * have only one parameter of type Event and no return type as every method call
 * is done asynchronously.
 *
 * @author Christian Lins
 * @author Kai Ritterbusch
 */
public interface ServerInterface {

    public static final boolean NO_NETWORK = true;

    static final int DEFAULT_PORT = 0xB000;


    /**
     * Client wants to login with the given username. This method is part one of
     * the Challenge Handshake Authentification Protocol (CHAP) and returns a
     * challenge that is valid for a few seconds (default: 30s). Event parameter
     * contains one parameter (String username). Server will call continueLogin
     * event method on client.
     * @param event
     */
    void login1(Event event);

    /**
     * Second part of the Challenge Handshake Authentification Protocol (CHAP).
     * If the login is successful the Server will transmit a Session object
     * through the given @see{ServerListenerInterface}. Event parameter contains
     * two parameter (String username, long hash). Server will call loggedIn
     * event method on client if successful.
     * @param event
     */
    void login2(Event event);

    /**
     * The client notfies the server that it was logged out (mostly implicit
     * through closing the client's frame). Event parameter contains one
     * parameter (Session session).
     * @param event
     */
    void logout(Event event);

    /**
     * Sends a chat message to the public channel. Event parameter contains two
     * parameter (Session session, String message).
     * @param event
     */
    void sendChatMessage(Event event);

    /**
     * Creates a new game on the server. Event parameter contains two parameter
     * (Session session, String gameName).
     * @param event
     */
    void createGame(Event event);

    /**
     * Player wants to continue a paused game.
     * @param event
     */
    void continueGame(Event event);

    /**
     * A player wants to join the game. Event parameter contains two parameter
     * (Session session, String gameName).
     * @param event
     */
    void joinGame(Event event);

    /**
     * A spectator wants to join the game. Event parameter contains two
     * parameter (Session session, String gameName).
     * @param event
     */
    void joinViewGame(Event event);

    /**
     * The creator of a game wants to start the game through his WaitingPanel.
     * The Server checks the session of validity and if the caller is indeed the
     * creator of the game. Event parameter contains two arguments (Session
     * session, String gameName).
     */
    void startGame(Event event);

    /**
     * Is called when a Client has pressed its moving keys. It is not possible
     * to move in both directions at once, so either x or y must be zero. If
     * they are both != zero the method will always return false. Event
     * parameter contains three arguments (Session session, int x, int y).
     * @param event
     */
    void move(Event event);

    /**
     * The player wants to pause the local game.
     * @param event
     */
    void pauseGame(Event event);

    /**
     * Is called when a client has pressed 'Space' which is usually the key for
     * placing a bomb. Event parameter contains one argument (Session session).
     */
    void placeBomb(Event event);

    /**
     * Is called when a client has pressed 'ESC' which is usually the key for
     * leaving a game Event parameter contains one argument (Session session).
     */
    void leaveGame(Event event);

    /**
     * Is called when server stops. Event parameter contains no arguments.
     */
    void logoutAll(Event event);

}
