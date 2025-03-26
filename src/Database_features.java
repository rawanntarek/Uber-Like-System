import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class Database_features {
    private static MongoDatabase db = Database.getDatabase("Uber_Like_System");
    public static void saveCustomer(ClientInfo user) {
        MongoCollection<Document> customers = db.getCollection("customers");
        if(user.getRole().equals("customer"))
        {
            Document customer=new Document("username",user.getUsername())
                    .append("password",user.getPassword())
                    .append("role",user.getRole());
            try {
                customers.insertOne(customer);
            } catch (Exception e) {
                System.out.println("MongoDB error: " + e.getMessage());
            }



        }
    }
    public static void saveDriver(ClientInfo user) {
        MongoCollection<Document> drivers = db.getCollection("drivers");
        if(user.getRole().equals("driver"))
        {
            Document customer=new Document("username",user.getUsername())
                    .append("password",user.getPassword())
                    .append("role",user.getRole());
            try {
                drivers.insertOne(customer);
            } catch (Exception e) {
                System.out.println("MongoDB error: " + e.getMessage());
            }



        }
    }
    public static void saveRide(Ride ride) {
        MongoCollection<Document> rides = db.getCollection("rides");

            Document ridee=new Document("ride id",ride.getRideId())
                    .append("customer username",ride.getCustomerUsername())
                    .append("assigned driver",ride.getAssignedDriver())
                    .append("ride status",ride.getStatus());
        try {
            rides.insertOne(ridee);
        } catch (Exception e) {
            System.out.println("MongoDB error: " + e.getMessage());
        }



        }
    }



