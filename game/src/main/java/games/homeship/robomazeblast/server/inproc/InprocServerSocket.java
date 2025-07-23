package games.homeship.robomazeblast.server.inproc;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 *
 * @author Christian Lins
 */
public class InprocServerSocket extends ServerSocket {
    public static final int PIPE_BUF_SIZE = 0x10000; // 16 KiB
    
    private final Map<Integer, ConnectionBundle> connections = new HashMap<>();
    private boolean connectionPending = false;
    private InprocSocketImpl newConnection = null;
    private int connectionPorts = 0;
    private final int listeningPort;
    
    public InprocServerSocket(int port) throws IOException {
        this.listeningPort = port;
        InprocConnectionManager.registerListener(port, this);
    }
    
    @Override
    public Socket accept() throws IOException {
        synchronized(connections) {
            try {
                while (!connectionPending) {
                    connections.wait();
                }
            } catch(InterruptedException ex) {
               throw new IOException("Interrupted", ex);
            }
            connectionPending = false;
            return newConnection;
        }
    }
    
    InprocSocketImpl connectNewClient() throws IOException {
        synchronized(connections) {
            
            // We use two piped i/o streams to connect client and server
            PipedInputStream clientInput = new PipedInputStream(PIPE_BUF_SIZE);
            PipedInputStream serverInput = new PipedInputStream(PIPE_BUF_SIZE);
            PipedOutputStream clientOutput = new PipedOutputStream(serverInput);
            PipedOutputStream serverOutput = new PipedOutputStream(clientInput);
            
            InprocSocketImpl clientSocket = new InprocSocketImpl(clientInput, clientOutput);
            InprocSocketImpl serverSocket = new InprocSocketImpl(serverInput, serverOutput);
            
            connectionPorts++;
            connections.put(connectionPorts, new ConnectionBundle(clientSocket, serverSocket));
            
            newConnection = serverSocket;
            connectionPending = true;
            
            connections.notifyAll();
            
            return clientSocket;
        }
    }
}
