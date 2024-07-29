package library.controller;

import library.model.LibraryModel;
import library.view.CheckoutView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class CheckoutController {
    private LibraryModel model;
    private CheckoutView view;

    public CheckoutController(LibraryModel model, CheckoutView view) {
        this.model = model;
        this.view = view;

        view.addConfirmListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.getCart().clearCart();
                view.displayMessage("Checkout confirmed! Thank you for your purchase.");
                view.dispose();
                
            }
        });

        /*view.addCloseListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.getCart().clearCart();
                view.dispose();
                
            }
        });*/
    }
}
