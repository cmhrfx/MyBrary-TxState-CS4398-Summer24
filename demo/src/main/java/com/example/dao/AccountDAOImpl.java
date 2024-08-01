package com.example.dao;

import com.example.LibraryDatabaseConnection;
import com.example.models.Account;
import com.example.models.LendingMaterial;
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
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate returnDate = currentDate.plusWeeks(3);

        for (LendingMaterial item : items) {
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
