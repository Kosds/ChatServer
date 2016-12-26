package chat;

import javax.swing.*;
import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

class Server implements Closeable{
    private static final int PORT = 4242;
    private Map<String, Client> clients;
    private DefaultListModel<String> clientsListModel;
    private ServerSocket serverSocket;
    private boolean isWork;

    Server() throws IOException{
        clients = new HashMap<>();
        serverSocket = new ServerSocket(PORT);
        clientsListModel = new DefaultListModel<>();
    }

    void start(){
        isWork = true;
        clients.clear();
    }
    synchronized void stop() throws IOException {
        for (Client client : clients.values())
            client.disconnect();
        isWork = false;
    }
    synchronized boolean isWork() {
        return isWork;
    }
    void waitConnection() throws IOException {
        Client client = new Client(serverSocket.accept());
        String nickname = client.getNickname();
        if(clients.containsKey(nickname)){
            client.sendMessage("error");
        } else {
            client.sendMessage("hello");
            clients.put(nickname, client);
            clientsListModel.addElement(nickname);
            sendAll(nickname + " joined the chat");
            new MessageWaiter(client).start();
        }
    }
    void disconnectUser(String nickname) throws IOException {
        clients.remove(nickname).disconnect();
        clientsListModel.removeElement(nickname);
    }
    DefaultListModel<String> getClientsModel() {
        return clientsListModel;
    }

    @Override
    public void close() throws IOException {
        stop();
        serverSocket.close();
    }
    class MessageWaiter extends Thread {
        Client client;
        String prefix;

        MessageWaiter(Client client) {
            this.client = client;
            prefix = client.getNickname() + ":\n";
        }

        @Override
        public void run() {
            while (client.isWork()) {
                try {
                    StringBuilder messageBuilder = new StringBuilder();
                    if (client.readMessage(messageBuilder)) {
                        String message = messageBuilder.toString();
                        if (message.equalsIgnoreCase("buy"))
                            disconnectUser(client.getNickname());
                        else
                            sendAll(prefix + message);
                    }
                } catch (IOException e) {
                    new ErrorDialog(null, e.toString());
                }
            }
        }
    }

    private void sendAll(String message) throws IOException {
        message = new StringBuilder(message)
                .append("\n")
                .insert(0, "\n")
                .toString();
        for (Client client: clients.values())
            client.sendMessage(message);
    }
}
