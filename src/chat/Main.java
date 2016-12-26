package chat;

import java.awt.*;

class Main {
    public static void main(String[] args){
        EventQueue.invokeLater(() -> new ServerFrame().createGUI());
    }
}
