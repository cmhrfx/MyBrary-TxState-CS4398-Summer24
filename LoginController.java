package library.controller;

import library.model.LibraryModel;
import library.model.User;
import library.view.BrowseView;
import library.view.LoginView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginController {
    private LibraryModel model;
    private LoginView loginView;

    public LoginController(LibraryModel model, LoginView loginView) {
        this.model = model;
        this.loginView = loginView;

        this.loginView.addLoginListener(new LoginListener());
    }

    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = loginView.getUsername();
            String password = loginView.getPassword();

            System.out.println("Attempting to authenticate user: " + username);

            User user = model.authenticate(username, password);
            if (user != null) {
                System.out.println("User authenticated: " + username);
                loginView.dispose();
                BrowseView browseView = new BrowseView(model, user);
                browseView.setVisible(true);
            } else {
                System.out.println("Authentication failed for user: " + username);
                loginView.showError("Invalid username or password.");
            }
        }
    }
}
