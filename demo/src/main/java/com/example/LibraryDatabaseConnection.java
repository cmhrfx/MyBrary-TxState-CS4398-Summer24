import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class LibraryDatabaseConnection {
    private static final String CONNECTION_STRING = "mongodb+srv://chris:bobcats24@cluster0.qxcbrdu.mongodb.net/";
    private static final String DATABASE_NAME = "Cluster0";
    private MongoClient mongoClient;
    private MongoDatabase database;

    public LibraryDatabaseConnection() {
        // Create a new MongoClient
        mongoClient = MongoClients.create(CONNECTION_STRING);
        // Connect to the database
        database = mongoClient.getDatabase(DATABASE_NAME);
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}