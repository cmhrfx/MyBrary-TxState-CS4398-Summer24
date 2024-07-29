package library.model;

import java.util.Date;

public abstract class Item {
    private String title;
    private Date dueDate;
    private boolean canBeCheckedOut;

    public Item(String title) {
        this.title = title;
        this.canBeCheckedOut = true;
    }

    public String getTitle() {
        return title;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public boolean canBeCheckedOut() {
        return canBeCheckedOut;
    }

    public void setCanBeCheckedOut(boolean canBeCheckedOut) {
        this.canBeCheckedOut = canBeCheckedOut;
    }

    public abstract double calculateOverdueFine(int daysOverdue);
}
