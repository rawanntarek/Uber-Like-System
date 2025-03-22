import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class UberServer {
    public static List<ClientInfo> customers = Collections.synchronizedList(new ArrayList<>());
    public static List<ClientInfo> drivers = Collections.synchronizedList(new ArrayList<>());
    public static List<User> users = Collections.synchronizedList(new ArrayList<>());
    public static List<Ride> rides = Collections.synchronizedList(new ArrayList<>());
    public static Map<String, DataOutputStream> driverOutputs = Collections.synchronizedMap(new HashMap<>());
    public static int rideIdCounter = 1;

    public static int customerId = 1;
    public static int driverId = 1;
    static {
        // Predefined admin user
        users.add(new User("admin", "admin123", "admin"));
    }
    public static synchronized int addCustomer(String address) {
        int id = customerId++;
        customers.add(new ClientInfo(id, "customer", address));
        return id;
    }

    public static synchronized int addDriver(String address, String username, DataOutputStream out) {
        int id = driverId++;
        drivers.add(new ClientInfo(id, "driver", address));
        driverOutputs.put(username,out);

        return id;
    }

    public static void main(String[] args) {

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
