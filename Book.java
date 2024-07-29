package library.model;

public class Book extends Item {
    private boolean isBestSeller;
    private boolean isReference;
    private String author;

    public Book(String title, String author, boolean isBestSeller, boolean isReference) {
        super(title);
        this.author = author;
        this.isBestSeller = isBestSeller;
        this.isReference = isReference;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isBestSeller() {
        return isBestSeller;
    }

    public boolean isReference() {
        return isReference;
    }

    @Override
    public double calculateOverdueFine(int overdueDays) {
        double fine = overdueDays * 0.10;
        return Math.min(fine, getMaxFine());
    }

    @Override
    public void checkout() {
        // Implementation of checkout logic
    }

    private double getMaxFine() {
        // Return the maximum fine, which could be the value of the book or some predefined value
        return 20.0; // Example value
    }
}
