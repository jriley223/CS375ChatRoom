package chatclient;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatClient extends JFrame implements ActionListener {

    private static ChatClient frame;
    private static JTextArea chatArea;
    private static JTextArea messageArea;
    private JButton sendButton;
    private String message = "";
    private static BufferedReader in;
    private static PrintWriter out;
    private static InetAddress host;
    private static Socket socket;
    private static String handle;

    public static void main(String[] args) throws IOException {

        frame = new ChatClient();
        frame.setTitle("A Simple Chat Client");
        frame.setSize(400, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.run();
    }

    public ChatClient() {
        chatArea = new JTextArea(20, 50);
        chatArea.setWrapStyleWord(true);
        chatArea.setLineWrap(true);
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());
        JLabel prompt = new JLabel("Enter Message: ");
        messageArea = new JTextArea(5, 50);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendPanel.add(prompt, BorderLayout.WEST);
        sendPanel.add(messageArea, BorderLayout.CENTER);
        sendPanel.add(sendButton, BorderLayout.EAST);
        add(sendPanel, BorderLayout.SOUTH);

        handle = JOptionPane.showInputDialog(frame,
                "Enter Your Nickname: ",
                "Name Entry",
                JOptionPane.PLAIN_MESSAGE);
        System.out.println("User name dialog input was: " + handle);
    }

    private void run() throws IOException {

        // Make connection and initialize streams
        host = InetAddress.getLocalHost();
        socket = new Socket(host, 52983);
        in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                out.println(handle);
            } else if (line.startsWith("NAMEACCEPTED")) {
                messageArea.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                chatArea.append(line.substring(7) + "\n");
            }
        }
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == sendButton) {
            System.out.println("User pressed SEND button.");
            message = messageArea.getText();
            System.out.println("User typed message: " + message);
            messageArea.setText("");
            if (message.equals("Bye")) {
                out.println(message);
                messageArea.setText("");
                try {
                    socket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                out.println(message);
                messageArea.setText("");
            }
        }
    }
}
