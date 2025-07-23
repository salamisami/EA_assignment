package games.homeship.robomazeblast.server.inproc;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

/**
 *
 * @author Christian Lins
 */
public class InprocSocketImpl extends Socket {
    private final PipedInputStream in;
    private final PipedOutputStream out;
    
    InprocSocketImpl(PipedInputStream in, PipedOutputStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }
    
    @Override
    public OutputStream getOutputStream() {
        return out;
    }
}
