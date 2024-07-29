package com.example;

import com.example.models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LibraryView {
    private UserDAO userDAO;
    private JTextField userText;
    private JPasswordField passwordText;
    private JLabel statusLabel;

    public LibraryView(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Library Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        frame.add(panel, BorderLayout.CENTER);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("User ID:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        userText = new JTextField(20);
        userText.setBounds(100, 20, 165, 25);
        panel.add(userText);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 165, 25);
        panel.add(passwordText);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(10, 110, 250, 25);
        panel.add(statusLabel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userText.getText();
                String password = new String(passwordText.getPassword());

                // Perform login
                if (userDAO.authenticateUser(userId, password)) {
                    statusLabel.setText("Login successful! Welcome, " + userId + "!");
                } else {
                    statusLabel.setText("Invalid user ID or password.");
                }
            }
        });
    }
}
