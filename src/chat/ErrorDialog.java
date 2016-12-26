package chat;

import javax.swing.*;
import java.awt.*;

class ErrorDialog extends JDialog {
    ErrorDialog(JFrame parent, String message){
        super(parent, "Error", true);
        setLayout(new BorderLayout(10, 10));
        JTextArea messageArea = new JTextArea(0, 30);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setText(message);
        messageArea.setEditable(false);
        add(messageArea, BorderLayout.CENTER);
        setResizable(false);
        pack();
        setVisible(true);
    }
}
