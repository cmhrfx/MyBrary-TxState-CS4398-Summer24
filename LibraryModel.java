package library.model;

import java.util.ArrayList;
import java.util.List;

public class LibraryModel {
    private List<User> users;
    private List<Item> items;

    public LibraryModel() {
        users = new ArrayList<>();
        items = new ArrayList<>();
        
        // Add some dummy data
        users.add(new User("admin", "123 Main St", "555-1234", "1", 30));
        users.add(new User("user1", "456 Elm St", "555-5678", "2", 10));
        
        // Add initial books
        items.add(new Book("1984", "George Orwell", false, false));
        items.add(new Book("To Kill a Mockingbird", "Harper Lee", true, false));
        items.add(new AudioVideo("The Matrix"));
        items.add(new Book("Brave New World", "Aldous Huxley", false, false));
        items.add(new Book("Moby Dick", "Herman Melville", false, false));
        items.add(new Book("The Great Gatsby", "F. Scott Fitzgerald", false, false));
        items.add(new Book("War and Peace", "Leo Tolstoy", false, false));
        items.add(new Book("Crime and Punishment", "Fyodor Dostoevsky", false, false));
        items.add(new Book("Pride and Prejudice", "Jane Austen", false, false));
        items.add(new Book("The Catcher in the Rye", "J.D. Salinger", false, false));
        items.add(new Book("The Hobbit", "J.R.R. Tolkien", true, false));
        items.add(new Book("The Lord of the Rings", "J.R.R. Tolkien", true, false));
        items.add(new Book("Harry Potter and the Sorcerer's Stone", "J.K. Rowling", true, false));
        items.add(new Book("The Da Vinci Code", "Dan Brown", true, false));
        items.add(new Book("The Alchemist", "Paulo Coelho", true, false));
        items.add(new Book("The Chronicles of Narnia", "C.S. Lewis", true, false));
    }

    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getName().equals(username) && user.getLibraryCardNumber().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void addItem(Item item) {
        items.add(item);
    }
}
