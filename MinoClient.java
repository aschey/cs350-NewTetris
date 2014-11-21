
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;

/**
 * CS 350
 * Project #6
 * Austin Schey
 * MinoClient.java: extends a MinoPanel to create a client
 */

public class MinoClient extends MinoPanel implements Runnable {
    private String host;
    public MinoClient(String host) {
        super();
        this.host = host;
    }

    @Override
    public void run() {
        this.connectToServer();
        this.getStreams();
        this.processConnection();
    }

    private void connectToServer() {
        try {
            this.createSocket(new Socket(InetAddress.getByName(this.host), 9999));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}