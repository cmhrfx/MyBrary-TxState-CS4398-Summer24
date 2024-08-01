package com.example.dao;

import com.example.LibraryDatabaseConnection;
import com.example.models.Account;
import com.example.models.LendingMaterial;
import com.example.models.Book;
import com.example.models.Movie;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

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

    public boolean returnLendedItem(String materialID, String accountID) {
        MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
        MongoCollection<Document> accountsCollection = getAccountsCollection();

        // Find and delete the item in the LendedItems collection
        Document foundItem = lendedItemsCollection.find(Filters.and(
            Filters.eq("MaterialID", materialID),
            Filters.eq("AccountID", accountID)
        )).first();
        
        if (foundItem != null) {
            lendedItemsCollection.deleteOne(foundItem);

            // Now, remove the item from the CheckedOutItems array in the Accounts collection
            accountsCollection.updateOne(
                Filters.eq("AccountID", accountID),
                Updates.pull("CheckedOutItems", new Document("MaterialID", materialID))
            );

            return true;
        }
        return false;
    }

}
