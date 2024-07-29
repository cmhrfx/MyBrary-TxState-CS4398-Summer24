package library.controller;

import library.model.Book;
import library.model.LibraryModel;
import library.view.BrowseView;
import library.view.CheckoutView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BrowseController {
    private LibraryModel model;
    private BrowseView view;

    public BrowseController(LibraryModel model, BrowseView view) {
        this.model = model;
        this.view = view;

        view.setBooks(model.getBooks());

        view.addAddToCartListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Book selectedBook = view.getSelectedBook();
                if (selectedBook != null) {
                    model.getCart().addBook(selectedBook);
                    view.displayMessage("Book added to cart!");
                } else {
                    view.displayMessage("No book selected!");
                }
            }
        });

        view.addCheckoutListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.setVisible(false);
                CheckoutView checkoutView = new CheckoutView();
                new CheckoutController(model, checkoutView);
                checkoutView.setCart(model.getCart().getBooks());
                checkoutView.setVisible(true);
            }
        });
    }
}
