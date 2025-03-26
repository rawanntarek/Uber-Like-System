import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class Database_features {
    private static MongoDatabase db = Database.getDatabase("Uber_Like_System");
    public static void saveCustomer(ClientInfo user) {
        MongoCollection<Document> customers = db.getCollection("customers");
        if(user.getRole().equals("customer"))
        {
            Document existingCustomer=customers.find(new Document("username",user.getUsername())).first();
            if(existingCustomer!=null)
            {
                return;
            }
            Document customer=new Document("id",user.getId())
                    .append("username",user.getUsername())
                    .append("password",user.getPassword())
                    .append("address",user.getAddress())
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
            Document existingDriver=drivers.find(new Document("username",user.getUsername())).first();
            if(existingDriver!=null)
            {
                return;
            }
            Document customer=new Document("id",user.getId())
                    .append("username",user.getUsername())
                    .append("password",user.getPassword())
                    .append("address",user.getAddress())
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
        Document existingRide=rides.find(new Document("ride id",ride.getRideId())).first();
        if(existingRide!=null)
        {
            return;
        }
            Document ridee=new Document("ride id",ride.getRideId())
                    .append("customer username",ride.getCustomerUsername())
                    .append("assigned driver",ride.getAssignedDriver())
                    .append("pickup",ride.getPickup())
                    .append("destination",ride.getDestination())
                    .append("ride status",ride.getStatus());
        try {
            rides.insertOne(ridee);
        } catch (Exception e) {
            System.out.println("MongoDB error: " + e.getMessage());
        }



        }
    public static void loadCustomers() {
        MongoCollection<Document> customers = db.getCollection("customers");
        int latest_id=0;
        for(Document customer : customers.find())
        {
            int id=customer.getInteger("id");
            String username=customer.getString("username");
            String password=customer.getString("password");
            String address=customer.getString("address");
            ClientInfo customerr=new ClientInfo(id,"customer",address,username,password);
            UberServer.customers.add(customerr);
            if(id>latest_id)
            {
                latest_id=id;
            }


        }
        UberServer.customerId=latest_id+1;
    }
    public static void loadDrivers() {
        MongoCollection<Document> drivers = db.getCollection("drivers");
        int latest_id=0;
        for(Document driver : drivers.find())
        {
            int id=driver.getInteger("id");
            String username=driver.getString("username");
            String password=driver.getString("password");
            String address=driver.getString("address");
            ClientInfo driverr=new ClientInfo(id,"driver",address,username,password);
            UberServer.drivers.add(driverr);
            if(id>latest_id)
            {
                latest_id=id;
            }


        }
        UberServer.driverId=latest_id+1;

    }
    public static void loadRides() {
        MongoCollection<Document> rides = db.getCollection("rides");
        int latest_id=0;
        for(Document ride : rides.find())
        {
            int id=ride.getInteger("ride id");
            String CustomerUsername=ride.getString("customer username");
            String assignedDriver=ride.getString("assigned driver");
            String rideStatus=ride.getString("ride status");
            String pickup=ride.getString("pickup");
            String destination=ride.getString("destination");
            Ride ridee=new Ride(id,CustomerUsername,pickup,destination);
            ridee.setAssignedDriver(assignedDriver);
            ridee.setStatus(rideStatus);
            UberServer.rides.add(ridee);
            if(id>latest_id)
            {
                latest_id=id;
            }

        }
        UberServer.rideIdCounter=latest_id+1;

    }
    }



