package vttp.mock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main( String[] args ) throws IOException {

        int port = 3000;
        List<String> paths = new LinkedList<>();
        paths.add("./target");

        // Retrieving relevant information from different commands
        if (args.length <= 2){
            switch(args[0]) {
                case "":
                    ;
                case "--port":
                    port = Integer.parseInt(args[1]);
                case "--docRoot":
                    // Array of paths where paths[0]=./target , path[1]=<2nd dir> ... 
                    String[] terms = args[1].split(":");
                    for (int i = 1; i < terms.length; i++) {
                        paths.add(terms[i]);
                    }
            } 
        // --port <port no.> --docRoot <dir>    
        } else if (args.length == 4) {
            port = Integer.parseInt(args[1]);
            // Array of paths where paths[0]=./target , path[1]=<2nd dir> ... 
            String[] terms = args[3].split(":");
            for (int i = 1; i < terms.length; i++) {
                paths.add(terms[i]);
            }
        }

        for (int i = 0; i < paths.size(); i++) {
            Path path = Paths.get(paths.get(i));
            boolean exists = Files.exists(path);
            boolean isDir = Files.isDirectory(path);
            boolean canRead = Files.isReadable(path);
            if (!exists) {
                System.err.printf("%s does not exist", paths.get(i));
                System.exit(1);
            } else if (!isDir) {
                System.err.printf("%s is not a directory", paths.get(i));
                System.exit(1);
            } else if (!canRead) {
                System.err.printf("%s cannot be read by server", paths.get(i));
                System.exit(1);
            }
        } 

        HttpServer server = new HttpServer(paths.get(0),port);
        server.start();
        

    }
}