import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UberServer {
    public static List<ClientInfo> customers = Collections.synchronizedList(new ArrayList<>());
    public static List<ClientInfo> drivers = Collections.synchronizedList(new ArrayList<>());
    public static int customerId = 1;
    public static int driverId = 1;
    public static synchronized int addCustomer(String address) {
        int id = customerId++;
        customers.add(new ClientInfo(id, "customer", address));
        return id;
    }
    public static synchronized int addDriver(String address) {
        int id = driverId++;
        drivers.add(new ClientInfo(id, "driver", address));
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
