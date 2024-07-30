public class LibraryCard {
    private String cardNumber;
    private boolean checkoutAllowed;
    private String phoneNumber;

    public LibraryCard(String cardNumber, boolean checkoutAllowed, String phoneNumber) {
        this.cardNumber = cardNumber;
        this.checkoutAllowed = checkoutAllowed;
        this.phoneNumber = phoneNumber;
    }

     public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public boolean isCheckoutAllowed() {
        return checkoutAllowed;
    }

    public void setCheckoutAllowed(boolean checkoutAllowed) {
        this.checkoutAllowed = checkoutAllowed;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phonNumber) {
        this.phoneNumber = phoneNumber
    }

}
