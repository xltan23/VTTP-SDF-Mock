package vttp.mock;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    // Setting up members
    private int port;
    private String directory;

    // Constructor
    public HttpServer(String path, int port) {
        this.directory = path;
        this.port = port;
    }

    public void start() {
        ServerSocket server;
        boolean stop = false;
        try {
            server = new ServerSocket(port);
            ExecutorService thrPool = Executors.newFixedThreadPool(3); 
            while(!stop) {
                System.out.println("Waiting for server connection");
                Socket sock = server.accept();
                System.out.println("Starting client connection");
                File repository = new File(directory);
                HttpClientConnection hcc = new HttpClientConnection(repository, sock);
                thrPool.execute(hcc);
            }
        } catch (IOException ex) {
            System.err.println("Server error, exiting");
            ex.printStackTrace();
        }
    }
}
