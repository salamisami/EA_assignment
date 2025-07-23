package games.homeship.robomazeblast.server.inproc;

/**
 *
 * @author Christian Lins
 */
class ConnectionBundle {
    private InprocSocketImpl clientSocket, serverSocket;
    
    public ConnectionBundle(InprocSocketImpl clientSocket, InprocSocketImpl serverSocket) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
    }
}
