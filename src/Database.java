import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Database {
    private static final String URL="mongodb+srv://rawaaan245:1234@cluster0.z96o2ea.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
    private static MongoClient client = null;

    public static MongoDatabase getDatabase(String dbName) {
        if (client == null) {
            client = MongoClients.create(URL);
        }
        return client.getDatabase(dbName);
    }
}
