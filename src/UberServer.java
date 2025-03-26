import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class UberServer {
    public static List<ClientInfo> customers = Collections.synchronizedList(new ArrayList<>());
    public static List<ClientInfo> drivers = Collections.synchronizedList(new ArrayList<>());
    public static List<Ride> rides = Collections.synchronizedList(new ArrayList<>());
    public static Map<String, DataOutputStream> driverOutputs = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, Boolean> driverAvailability = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, DataOutputStream> customerOutputs = Collections.synchronizedMap(new HashMap<>());
    public static Map<String, Offer> pendingCustomerOffers = Collections.synchronizedMap(new HashMap<>());


    public static int rideIdCounter = 1;

    public static int customerId = 1;
    public static int driverId = 1;
    public static synchronized int addCustomer(String address, String username, String password,DataOutputStream out) {
        int id = customerId++;
        customers.add(new ClientInfo(id, "customer", address,username,password));
        customerOutputs.put(username,out);
        return id;
    }

    public static synchronized int addDriver(String address, String username, String password,DataOutputStream out) {
        int id = driverId++;
        drivers.add(new ClientInfo(id, "driver", address,username,password));
        driverOutputs.put(username,out);
        driverAvailability.put(username,true);

        return id;
    }

    public static void main(String[] args) {
        UberServer.customers.clear();
        UberServer.drivers.clear();
        UberServer.rides.clear();

        Database_features.loadCustomers();
        Database_features.loadDrivers();
        Database_features.loadRides();
        try {
            ServerSocket serverSocket = new ServerSocket(6660);
            System.out.println("Server started, and waiting for a client ...");
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client accepted/connected");

                ClientHandler clientHandler=new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();

            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}