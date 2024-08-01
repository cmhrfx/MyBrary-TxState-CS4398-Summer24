package com.example.dao;

import com.example.LibraryDatabaseConnection;
import com.example.dao.LendingMaterialDAO;
import com.example.models.Account;
import com.example.models.LendingMaterial;
import com.example.models.Book;
import com.example.models.Journal;
import com.example.models.LibraryCard;
import com.example.models.Magazine;
import com.example.models.Movie;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.bson.types.ObjectId;


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

    private MongoCollection<Document> getLendingMaterialsCollection() {
        return database.getCollection("LendingMaterial");
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
                .append("LastFeeAccruedDate", "")
                .append("BeenRenewed", false)
                .append("FeeAccrued", 0.0)
                .append("DaysOverDue", 0);
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
    public void setAccountBalance(String accountId, double amount) {
        MongoCollection<Document> accountsCollection = getAccountsCollection();
        accountsCollection.updateOne(
            Filters.eq("AccountID", accountId),
            new Document("$set", new Document("Balance", amount))
        );
    }


    @Override
    public void updateLendedItemFees() {
        MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
        if (lendedItemsCollection != null) {
            System.out.println("Lended items Collection Found!");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
    
        lendedItemsCollection.find().forEach((Consumer<Document>) item -> {
            LocalDate returnDate = LocalDate.parse(item.getString("ReturnDate"), formatter);
            int daysOverdue = (int) ChronoUnit.DAYS.between(returnDate, today);

            if (daysOverdue < 0) {
                daysOverdue = 0;
            }
            
            String lastFeeAccruedDateStr = item.getString("LastFeeAccruedDate");
            LocalDate lastFeeAccruedDate = lastFeeAccruedDateStr.isEmpty() ? returnDate : LocalDate.parse(lastFeeAccruedDateStr, formatter);
            int daysSinceLastAccrued = (int) ChronoUnit.DAYS.between(lastFeeAccruedDate, today);

            if (daysSinceLastAccrued < 0) {
                daysSinceLastAccrued = 0;
            }
    
            String materialID = item.getString("MaterialID");
            LendingMaterial lendingMaterial = getLendingMaterialById(materialID);
            if (lendingMaterial == null) {
                System.out.println("No lending material found for MaterialID: " + materialID);
                return; // Skip to the next item if the material is not found
            }
    
            // Skip fee accrual for Magazine and Journal
            if (lendingMaterial instanceof Magazine || lendingMaterial instanceof Journal) {
                System.out.println("Skipping fee accrual for: " + lendingMaterial.getTitle());
                return;
            }
    
            double itemValue = lendingMaterial.getValue();
    
            double currentFeeAccrued = item.getDouble("FeeAccrued");
            double potentialIncrement = daysSinceLastAccrued * 0.10;
            double newFeeAccrued = currentFeeAccrued + potentialIncrement;
    
            if (newFeeAccrued > itemValue) {
                newFeeAccrued = itemValue;
            }
    
            lendedItemsCollection.updateOne(Filters.eq("_id", item.getObjectId("_id")),
                Updates.combine(
                    Updates.set("DaysOverDue", daysOverdue),
                    Updates.set("FeeAccrued", newFeeAccrued),
                    Updates.set("LastFeeAccruedDate", today.format(formatter))
                )
            );
        });
    }
    


    @Override
    public void updateAccountBalance() {
        MongoCollection<Document> lendedItemsCollection = getLendedItemsCollection();
        MongoCollection<Document> accountsCollection = getAccountsCollection();
    
        accountsCollection.find().forEach((Consumer<Document>) account -> {
            String accountId = account.getString("AccountID");
            
            // Find all lended items for this account
            List<Document> lendedItems = lendedItemsCollection.find(Filters.eq("AccountID", accountId)).into(new ArrayList<>());
            
            // Sum the fees accrued for each lended item
            double totalFees = lendedItems.stream()
                .mapToDouble(item -> item.getDouble("FeeAccrued"))
                .sum();
            
            // Update the account balance
            accountsCollection.updateOne(Filters.eq("AccountID", accountId),
                Updates.set("Balance", totalFees)
            );
        });
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

    public LendingMaterial getLendingMaterialById(String materialID) {
        System.out.println("Searching for materialID: " + materialID);
        
        // Check the collection name and connection
        MongoCollection<Document> collection = getLendingMaterialsCollection();
        if (collection == null) {
            System.out.println("LendingMaterials collection is null. Check the database connection and collection name.");
            return null;
        }
    
        // Search for the document
        Document doc = collection.find(Filters.eq("MaterialID", materialID)).first();
        if (doc == null) {
            System.out.println("No document found with MaterialID: " + materialID);
            return null;
        }
    
        System.out.println("Document found: " + doc.toJson());
    
        // Check for the type field
        String type = doc.getString("Type"); // Ensure the field name is correct
        if (type == null) {
            System.out.println("Type field is missing in the document: " + doc.toJson());
            return null;
        }
    
        // Process the document based on type
        switch (type) {
            case "Book":
                return Book.fromDocument(doc);
            case "Movie":
                return Movie.fromDocument(doc);
            case "Magazine":
                return Magazine.fromDocument(doc);
            case "Journal":
                return Journal.fromDocument(doc);
            default:
                throw new IllegalArgumentException("Unknown type: " + type);
        }
    }
    
}
