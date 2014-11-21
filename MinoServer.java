import java.io.IOException;
import java.net.ServerSocket;

/**
 * CS 350
 * Project #6
 * Austin Schey
 * MinoServer.java: extends a MinoPanel to create a server
 */

public class MinoServer extends MinoPanel implements Runnable {
    private ServerSocket server;

    @Override
    public void run() {
        try {
            this.server = new ServerSocket(9999, 100);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        this.waitForConnection();
        this.getStreams();
        this.processConnection();

    }

    private void waitForConnection() {
        try {
            this.createSocket(this.server.accept());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
