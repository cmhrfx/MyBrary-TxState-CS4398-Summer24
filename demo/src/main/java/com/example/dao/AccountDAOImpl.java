package com.example.dao;

import com.example.LibraryDatabaseConnection;
import com.example.models.Account;
import com.example.models.LendingMaterial;
import com.example.models.Book;
import com.example.models.LibraryCard;
import com.example.models.Movie;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AccountDAOImpl implements AccountDAO {
    private MongoDatabase database;

    public AccountDAOImpl(LibraryDatabaseConnection connection) {
        this.database = connection.getDatabase();
    }

    private MongoCollection<Document> getAccountsCollection() {
        return database.getCollection("Accounts");
    }

    private MongoCollection<Document> getLendedItemsCollection() {
        return database.getCollection("LendedItems");
    }

    private MongoCollection<Document> getReservationsCollection() {
        return database.getCollection("Reservations");
    }

    private MongoCollection<Document> getLibraryCardCollection() {
        return database.getCollection("LibraryCards");
    }

    @Override
    public Account getAccountById(String accountId) {
        Document doc = getAccountsCollection().find(Filters.eq("AccountID", accountId)).first();
        if (doc != null) {
            return Account.fromDocument(doc);
        }
        return null;
    }

    @Override
    public void insertAccount(Account account) {
        getAccountsCollection().insertOne(account.toDocument());
    }

    @Override
    public void updateAccount(Account account) {
        getAccountsCollection().updateOne(Filters.eq("AccountID", account.getAccountId()), new Document("$set", account.toDocument()));
    }

    @Override
    public void deleteAccount(int accountId) {
        getAccountsCollection().deleteOne(Filters.eq("AccountID", accountId));
    }

    @Override
    public void updateLendedItems(List<LendingMaterial> items, String accountId) {
        MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate currentDate = LocalDate.now();
        for (LendingMaterial item : items) {
            LocalDate returnDate = getReturnDate(item);
            Document lendedItemDoc = new Document("MaterialID", item.getMaterialID())
                .append("AccountID", accountId)
                .append("LendedDate", currentDate.format(formatter))
                .append("ReturnDate", returnDate.format(formatter))
                .append("LastBalanceUpdate", "")
                .append("BeenRenewed", false)
                .append("DaysOverdue", 0);
            lendedItemsCollection.insertOne(lendedItemDoc);
        }
    }

    @Override
    public List<Document> getAllLendedItems() {
        MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
        List<Document> lendedItems = new ArrayList<>();
        lendedItemsCollection.find().into(lendedItems);
        return lendedItems;
    }

    @Override
    public void updateLendedItemDaysOverdue(Document item, long daysOverdue) {
        MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
        lendedItemsCollection.updateOne(
            Filters.eq("_id", item.getObjectId("_id")),
            new Document("$set", new Document("DaysOverdue", daysOverdue))
        );
    }

    @Override
    public void updateLendedItemLastBalanceUpdate(Document item, LocalDate lastBalanceUpdate) {
        MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        lendedItemsCollection.updateOne(
            Filters.eq("_id", item.getObjectId("_id")),
            new Document("$set", new Document("LastBalanceUpdate", lastBalanceUpdate.format(formatter)))
        );
    }

    @Override
    public void incrementAccountBalance(String accountId, double amount) {
        MongoCollection<Document> accountsCollection = getAccountsCollection();
        accountsCollection.updateOne(
            Filters.eq("AccountID", accountId),
            new Document("$inc", new Document("Balance", amount))
        );
    }

    @Override
    public void reserveItem(String accountId, LendingMaterial item) {
        MongoCollection<Document> reservationsCollection = getReservationsCollection();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate availableDate = getAvailableDate(item);

        Document reservationsDoc = new Document("MaterialID", item.getMaterialID())
            .append("AccountID", accountId)
            .append("ReservedOn", currentDate.format(formatter))
            .append("AvailableOn", availableDate.format(formatter));
        reservationsCollection.insertOne(reservationsDoc);
    }

    @Override
    public LocalDate getReturnDate(LendingMaterial item) {
        LocalDate returnDate;
        LocalDate currentDate = LocalDate.now();
        if (item instanceof Book) {
            Book book = (Book) item;
            if (book.getBestSeller() == true) {
                returnDate = currentDate.plusWeeks(2);
            } else {
                returnDate = currentDate.plusWeeks(3);
            }
        } else if (item instanceof Movie) {
            returnDate = currentDate.plusWeeks(2);
        } else {
            System.out.println("Bad returnDate definition! returning localDate!");
            returnDate = currentDate;
        }
        return returnDate;
    }

    @Override
    public LocalDate getAvailableDate(LendingMaterial lendingMaterial) {
        List<Document> lendedItems = getAllLendedItems();
        List<Document> reservations = getAllReservations();
        LocalDate latestAvailableOnDate = null;
        LocalDate nearestReturnDate = null;
    
        // Search reservations for the latest AvailableOn date for the material
        for (Document reservation : reservations) {
            if (reservation.getString("MaterialID").equals(lendingMaterial.getMaterialID())) {
                LocalDate availableOn = LocalDate.parse(reservation.getString("AvailableOn"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (latestAvailableOnDate == null || availableOn.isAfter(latestAvailableOnDate)) {
                    latestAvailableOnDate = availableOn;
                }
            }
        }
    
        // If not reserved, search lended items for the nearest ReturnDate
        if (latestAvailableOnDate == null) {
            for (Document lendedItem : lendedItems) {
                if (lendedItem.getString("MaterialID").equals(lendingMaterial.getMaterialID())) {
                    LocalDate returnDate = LocalDate.parse(lendedItem.getString("ReturnDate"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    if (nearestReturnDate == null || returnDate.isBefore(nearestReturnDate)) {
                        nearestReturnDate = returnDate;
                    }
                }
            }
        } else {
            // If reserved, calculate the return date based on the latest available date
            nearestReturnDate = getReturnDateFromAvailableOn(latestAvailableOnDate, lendingMaterial);
        }
    
        // Return the appropriate date
        if (latestAvailableOnDate != null) {
            return nearestReturnDate;
        } else if (nearestReturnDate != null) {
            return nearestReturnDate;
        } else {
            // If no reservations or lended items found, it's available immediately
            return LocalDate.now();
        }
    }

    private LocalDate getReturnDateFromAvailableOn(LocalDate availableOn, LendingMaterial item) {
        LocalDate returnDate;
        if (item instanceof Book) {
            Book book = (Book) item;
            if (book.getBestSeller()) {
                returnDate = availableOn.plusWeeks(2);
            } else {
                returnDate = availableOn.plusWeeks(3);
            }
        } else if (item instanceof Movie) {
            returnDate = availableOn.plusWeeks(2);
        } else {
            returnDate = availableOn; // Default case, assuming no specific return date
        }
        return returnDate;
    }

    @Override
    public List<Document> getAllReservations() {
        MongoCollection<Document> reservationsCollection = getReservationsCollection();
        List<Document> reservations = new ArrayList<>();
        reservationsCollection.find().into(reservations);
        return reservations;
    }

    @Override
    public LibraryCard getLibraryCard(String accountId) {
        MongoCollection<Document> libraryCardCollection = getLibraryCardCollection();
        
        // Query the collection for the document with the given userId
        Document libraryCardDoc = libraryCardCollection.find(Filters.eq("AccountID", accountId)).first();
        // Convert the document to a LibraryCard object if found
        if (libraryCardDoc != null) {
            System.out.println("library card found!");
            return LibraryCard.fromDocument(libraryCardDoc); // Assuming you have a fromDocument method in LibraryCard class
        }
        System.out.println("No library card found!");
        return null; // Return null if no document is found
    }

    @Override
    public Document getLendedItemById(String materialID, String accountID) {
        MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
        return lendedItemsCollection.find(Filters.and(
            Filters.eq("MaterialID", materialID),
            Filters.eq("AccountID", accountID)
        )).first();
    }

    @Override
    public void updateLendedItemReturnDate(String materialID, String accountID, LocalDate newReturnDate) {
    MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    lendedItemsCollection.updateOne(
        Filters.and(
            Filters.eq("MaterialID", materialID),
            Filters.eq("AccountID", accountID)
        ),
        new Document("$set", new Document("ReturnDate", newReturnDate.format(formatter)))
        );
    }

    @Override
    public Boolean reservationExists(LendingMaterial lendingMaterial) {
        MongoCollection<Document> reservationsCollection = getReservationsCollection();
        Document reservation = reservationsCollection.find(Filters.eq("MaterialID", lendingMaterial.getMaterialID())).first();
        return reservation != null;
    }

    @Override
    public void setLendedItemBeenRenewed(String materialID, String accountID, boolean beenRenewed) {
        MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
        lendedItemsCollection.updateOne(
            Filters.and(
                Filters.eq("MaterialID", materialID),
                Filters.eq("AccountID", accountID)
            ),
            new Document("$set", new Document("BeenRenewed", beenRenewed))
        );
    }

    @Override
    public boolean returnLendedItem(String materialID, String accountID) {
        MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
        MongoCollection<Document> accountsCollection = getAccountsCollection();

        // Find and delete the item in the LendedItems collection
        Document foundItem = lendedItemsCollection.find(Filters.and(
            Filters.eq("MaterialID", materialID),
            Filters.eq("AccountID", accountID)
        )).first();

        if (foundItem != null) {
            System.out.println("Found lended item: " + foundItem);
            lendedItemsCollection.deleteOne(foundItem);
            System.out.println("Deleted lended item from LendedItems collection");

            // Now, remove the item from the CheckedOutItems array in the Accounts collection
            UpdateResult updateResult = accountsCollection.updateOne(
                Filters.eq("AccountID", accountID),
                Updates.pull("CheckedOutItems", new Document("MaterialID", materialID))
            );

            if (updateResult.getModifiedCount() > 0) {
                System.out.println("Deleted item from CheckedOutItems in Accounts collection");
                return true;
            } else {
                System.out.println("Failed to delete item from CheckedOutItems in Accounts collection");
            }
        } else {
            System.out.println("No lended item found with MaterialID: " + materialID + " and AccountID: " + accountID);
        }
        return false;
    }

    
}
