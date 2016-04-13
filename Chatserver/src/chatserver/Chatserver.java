package chatserver;

/**
 *
 * @author justinriley
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class Chatserver {

    private static final int PORT = 52983;
    // The set of all names of clients in the chat room. 
    private static HashSet<String> handles = new HashSet<String>();
    // The set of all the print writers for all the clients.  
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    //A thread handler for broadcasting a single client's message to all clients. 
    private static class Handler extends Thread {

        private String handle;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }
       //Sets a datastream to a specific handle and broadcasts the inputs
        // from one handle to the rest of the clients.

        public void run() {
            try {
                // Create character streams for the socket.
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    out.println("SUBMITNAME");
                    handle = in.readLine();
                    if (handle == null) {
                        return;
                    }
                    synchronized (handles) {
                        if (!handles.contains(handle)) {
                            handles.add(handle);
                            break;
                        }
                    }
                }
                out.println("NAMEACCEPTED");
                writers.add(out);

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    for (PrintWriter writer : writers) 
                    if (input == "Bye"){
                    writer.println("MESSAGE" + handle + " Has Left the Chatroom" );
                    }
                    else{
                        writer.println("MESSAGE" + handle + ": " + input);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                if (handle != null) {
                    handles.remove(handle);
                }
                if (out != null) {
                    writers.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
