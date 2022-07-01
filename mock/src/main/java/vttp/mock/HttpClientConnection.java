package vttp.mock;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class HttpClientConnection implements Runnable {

    private File repository;
    private Socket sock;
    private InputStream is;
    private DataInputStream dis;
    private OutputStream os;
    private DataOutputStream dos;

    public HttpClientConnection(File repo, Socket sock) {
        this.repository = repo;
        this.sock = sock;
    }

    public void run() {
        try {
            start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void start() throws IOException {
        boolean stop = false;
        initializeStreams(sock);
        while (!stop) {
            String input = read();
            System.out.printf(">> %s", input);

            String[] terms = input.split(" ");
            String msg = "";
            
            // Action 1: Checking GET method
            if (terms[0].equals("GET")) {
                
                if (terms[1].equals("/")) {
                    terms[1].replaceAll("/", "/index.html");
                }
                Path path = Paths.get(terms[1]);
                byte[] arr;
                if (Files.exists(path)) {
                    // Perform operation
                    String line = terms[1].replaceAll(".", " ");
                    if (line.endsWith("png")) {
                        arr = Files.readAllBytes(path);
                        msg = "HTTP/1.1 200 OK\r\n Content-Type: image/png\r\n \r\n %s".formatted(Arrays.toString(arr));
                        write(msg);
                    } else {
                        arr = Files.readAllBytes(path);
                        msg = "HTTP/1.1 200 OK\r\n \r\n %s".formatted(Arrays.toString(arr));
                        write(msg);
                    }
                } else {
                    // Set up response message if path does not exist
                    msg = "HTTP/1.1 404 Not Found\r\n \r\n %s not found\r\n".formatted(terms[0]);
                    write(msg);
                    stop = true;
                }
            } else { 
                // Set up response message if method is not allowed
                msg = "HTTP/1.1 405 Method Not Allowed\r\n \r\n %s not supported\r\n".formatted(terms[0]);
                write(msg);
                stop = true;
            }
        }
        close();
    }

    private String read() throws IOException {
        return dis.readUTF();
    }

    private void write(String out) throws IOException {
        dos.writeUTF(out);
        dos.flush();
    }

    public void initializeStreams(Socket sock) throws IOException {
        is = sock.getInputStream();
        dis = new DataInputStream(is);
        os = sock.getOutputStream();
        dos = new DataOutputStream(os);
    }

    public void close() throws IOException {
        is.close();
        os.close();
    }
    
}
