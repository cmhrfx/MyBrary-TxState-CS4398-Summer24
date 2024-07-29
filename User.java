package library.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String address;
    private String phoneNumber;
    private String libraryCardNumber;
    private int age;
    private List<Item> checkedOutItems;

    public User(String name, String address, String phoneNumber, String libraryCardNumber, int age) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.libraryCardNumber = libraryCardNumber;
        this.age = age;
        this.checkedOutItems = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLibraryCardNumber() {
        return libraryCardNumber;
    }

    public int getAge() {
        return age;
    }

    public List<Item> getCheckedOutItems() {
        return checkedOutItems;
    }

    public void checkoutItem(Item item) throws Exception {
        if (age <= 12 && checkedOutItems.size() >= 5) {
            throw new Exception("Children can only check out five items at a time.");
        }
        checkedOutItems.add(item);
        item.checkout();
    }

    public void returnItem(Item item) {
        checkedOutItems.remove(item);
    }

    public double calculateOverdueFines() {
        double totalFines = 0.0;
        for (Item item : checkedOutItems) {
            long diffInMillies = Math.abs(item.getDueDate().getTime() - System.currentTimeMillis());
            int daysOverdue = (int) (diffInMillies / (1000 * 60 * 60 * 24));
            totalFines += item.calculateOverdueFine(daysOverdue);
        }
        return totalFines;
    }
}
