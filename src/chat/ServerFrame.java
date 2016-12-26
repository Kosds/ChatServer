package chat;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

class ServerFrame extends JFrame {
    private JList<String> clients;
    private JButton start;
    private JButton stop;
    private JButton disconnect;
    private Server server;

    ServerFrame(){
        super("Server");
        start = new JButton("Start");
        stop = new JButton("Stop");
        disconnect = new JButton("Disconnect");
        try {
            server = new Server();
        }catch (IOException e){
            new ErrorDialog(this, e.toString());
        }
        clients = new JList<>(server.getClientsModel());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    void createGUI(){
        clients.addListSelectionListener(e -> disconnect.setEnabled(true));
        start.addActionListener(e -> serverStart());
        stop.addActionListener(e -> serverStop());
        disconnect.addActionListener(e -> disconnectClient());
        disconnect.setEnabled(false);

        JPanel controlPanel = new JPanel();
        controlPanel.add(start);
        controlPanel.add(disconnect);
        controlPanel.add(stop);

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(new JScrollPane(clients), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        setResizable(false);
        pack();
    }

    private void connectionsReception(){
        while(server.isWork()) {
            try {
                server.waitConnection();
                clients.revalidate();
                clients.repaint();
            } catch (IOException e){
                new ErrorDialog(null, e.toString());
            }
        }
    }
    private void serverStart(){
        clients.removeAll();
        server.start();
        new Thread(this::connectionsReception).start();
        stop.setEnabled(true);
        start.setEnabled(false);
    }
    private void serverStop(){
        try{
            server.stop();
        }catch (IOException e){
            new ErrorDialog(this, e.toString());
        }
        stop.setEnabled(false);
        start.setEnabled(true);
    }
    private void disconnectClient(){
        try {
            server.disconnectUser(clients.getSelectedValue());
            disconnect.setEnabled(false);
        } catch (IOException e){
            new ErrorDialog(this, e.toString());
        }
    }
}
