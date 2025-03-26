import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class Database_features {
    private static MongoDatabase db = Database.getDatabase("Uber_App");
    public static void saveCustomer(User user) {
        MongoCollection<Document> customers = db.getCollection("customers");
        if(user.getRole().equals("customer"))
        {
            Document customer=new Document("username",user.getUsername())
                    .append("password",user.getPassword())
                    .append("role",user.getRole());
            customers.insertOne(customer);



        }
    }
    public static void saveDriver(User user) {
        MongoCollection<Document> drivers = db.getCollection("drivers");
        if(user.getRole().equals("driver"))
        {
            Document customer=new Document("username",user.getUsername())
                    .append("password",user.getPassword())
                    .append("role",user.getRole());
            drivers.insertOne(customer);



        }
    }
    public static void saveRide(Ride ride) {
        MongoCollection<Document> rides = db.getCollection("rides");

            Document customer=new Document("ride id",ride.getRideId())
                    .append("customer username",ride.getCustomerUsername())
                    .append("assigned driver",ride.getAssignedDriver())
                    .append("ride status",ride.getStatus());
            rides.insertOne(customer);



        }
    }



