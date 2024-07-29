package library;

import library.controller.LoginController;
import library.model.LibraryModel;
import library.view.LoginView;

public class Main {
    public static void main(String[] args) {
        // Initialize the model
        LibraryModel model = new LibraryModel();

        // Initialize the login view
        LoginView loginView = new LoginView();

        // Initialize the login controller
        new LoginController(model, loginView);

        // Make the login view visible
        loginView.setVisible(true);
    }
}
