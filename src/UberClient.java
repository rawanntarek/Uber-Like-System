import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class UberClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 6660);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);

            System.out.println(input.readUTF());
            String action = scanner.nextLine();
            output.writeUTF(action);

            System.out.print(input.readUTF());
            String username = scanner.nextLine();
            output.writeUTF(username);

            System.out.print(input.readUTF());
            String password = scanner.nextLine();
            output.writeUTF(password);

            if (action.equalsIgnoreCase("register")) {
                System.out.print(input.readUTF());
                String role = scanner.nextLine();
                output.writeUTF(role);
            }

            String response = input.readUTF();
            System.out.println("Server: " + response);
            if (response.contains("Disconnecting")) return;

            String role = "";
            if (response.toLowerCase().contains("customer")) role = "customer";
            else if (response.toLowerCase().contains("driver")) role = "driver";
            else if (response.toLowerCase().contains("admin")) role = "admin";

            if (role.equals("driver")) {
                new Thread(() -> {
                    try {
                        while (true) {
                            String serverMsg = input.readUTF();
                            System.out.println( serverMsg);

                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected from server.");
                    }
                }).start();

                int choice = 0;
                while (choice != 3) {
                    System.out.println("Driver Menu:");
                    System.out.println("1. Offer a fare for a ride request");
                    System.out.println("2. Send status updates of the ride (start/end)");
                    System.out.println("3. Disconnect from the server.");
                    System.out.print("Choose an option: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            System.out.print("Offer a fare: ");
                            int fare = scanner.nextInt();
                            scanner.nextLine();
                            output.writeUTF("fare: " + fare);
                            System.out.println("Server: " + input.readUTF());
                            break;
                        case 2:
                            System.out.print("Enter ride status (start/end): ");
                            String status = scanner.nextLine();
                            output.writeUTF(status);
                            System.out.println("Server: " + input.readUTF());
                            break;
                        case 3:
                            output.writeUTF("exit");
                            System.out.println("Disconnecting...");
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }
                }

            } else if (role.equals("customer")) {

                new Thread(() -> {
                    try {
                        while (true) {
                            String msg = input.readUTF();
                            System.out.println("\n📢 Server: " + msg);
                            System.out.print("Choose an option: ");
                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected from server.");
                    }
                }).start();

                int choice = 0;
                while (choice != 3) {
                    System.out.println("Customer Menu:");
                    System.out.println("1. Request a ride");
                    System.out.println("2. View ride status");
                    System.out.println("3. Disconnect from server.");
                    System.out.print("Choose an option: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            System.out.print("Enter Pickup Location: ");
                            String pickup = scanner.nextLine();
                            System.out.print("Enter Destination: ");
                            String dest = scanner.nextLine();
                            output.writeUTF("pickupLocation: " + pickup + "\ndestination: " + dest);
                            break;
                        case 2:
                            output.writeUTF("viewStatus");
                            break;
                        case 3:
                            output.writeUTF("exit");
                            System.out.println("Disconnecting...");
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }
                }
            }
 else if (role.equals("admin")) {
                System.out.println("Admin menu coming soon...");
            }

            scanner.close();
            output.close();
            input.close();
            socket.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
