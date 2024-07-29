import java.util.ArrayList;

public class Member extends LibraryCard {
    private String name;
    private String id;
    private String address;
    private int age;
    private LibraryCard libraryCard;
    private ArrayList<String> checkedOutItems;

    public Member(String name, String id, String address, int age, LibraryCard libraryCard) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.age = age;
        this.libraryCard = libraryCard;
        this.checkedOutItems = new ArrayList<>();
    }

    public boolean canCheckOutItems() {
        if (age < 12) {
            return checkedOutItems.size() < 5;
        }
        else 
            {return true;}
    }

     public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int age() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    
}
