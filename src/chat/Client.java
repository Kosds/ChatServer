package chat;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Client implements Closeable{
    private Socket socket;
    private String nickname;
    private DataInputStream in;
    private DataOutputStream out;

    Client(Socket socket) throws IOException{
        this.socket = socket;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
        String[] responseParts = in.readUTF().split(": ");
        if(responseParts[0].equalsIgnoreCase("nickname"))
            nickname = responseParts[1];
        else
            out.writeUTF("write a nickname");
    }

    String getNickname() {
        return nickname;
    }
    boolean readMessage(StringBuilder sb){
        try{
            sb.delete(0, sb.length()).append(in.readUTF());
        }catch (IOException e){
            return false;
        }
        return true;
    }
    void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }
    void disconnect() throws IOException{
        out.writeUTF("buy");
        close();
    }
    synchronized boolean isWork(){
        return !socket.isClosed() && socket.isConnected();
    }
    @Override
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
