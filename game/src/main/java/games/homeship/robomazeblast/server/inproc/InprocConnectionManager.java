package games.homeship.robomazeblast.server.inproc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Christian Lins
 */
public class InprocConnectionManager {
    private static final Map<Integer, InprocServerSocket> listener = new HashMap<>();
    
    static void registerListener(int port, InprocServerSocket ssocket) {
        synchronized(listener) {
            listener.put(port, ssocket);
        }
    }
    
    public static InprocSocketImpl connect(int port) throws IOException {
        synchronized(listener) {
            if (listener.containsKey(port)) {
                var ssocket = listener.get(port);
                return ssocket.connectNewClient();
            } else {
                throw new IOException("Could not connect to server: not found");
            }
        }
    }
}
