import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class UberClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 6660);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            //equalsIgnoreCase() is a method in Java that compares two strings ignoring case sensitivity.
            Scanner scanner = new Scanner(System.in);
//
            // Handle login/register
            System.out.println(dataInputStream.readUTF()); // Do you want to login or register?
            String action = scanner.nextLine();
            dataOutputStream.writeUTF(action);

            System.out.print(dataInputStream.readUTF()); // Enter username:
            String username = scanner.nextLine();
            dataOutputStream.writeUTF(username);

            System.out.print(dataInputStream.readUTF()); // Enter password:
            String password = scanner.nextLine();
            dataOutputStream.writeUTF(password);

            if (action.equalsIgnoreCase("register")) {
                System.out.print(dataInputStream.readUTF()); // Are you a customer or driver?
                String role = scanner.nextLine();
                dataOutputStream.writeUTF(role);
            }

            String response = dataInputStream.readUTF();
            System.out.println("Server: " + response);
            if (response.contains("Disconnecting")) return;

            // Determine role
            String role = "";
            if (response.toLowerCase().contains("customer")) {
                role = "customer";
            } else if (response.toLowerCase().contains("driver")) {
                role = "driver";
            } else if (response.toLowerCase().contains("admin")) {
                role = "admin";
            }

            if (role.equals("driver")) {
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
                            dataOutputStream.writeUTF("fare: " + fare);
                            System.out.println("Server: " + dataInputStream.readUTF());
                            break;
                        case 2:
                            System.out.print("Enter ride status (start/end): ");
                            String status = scanner.nextLine();
                            dataOutputStream.writeUTF(status);
                            System.out.println("Server: " + dataInputStream.readUTF());
                            break;
                        case 3:
                            dataOutputStream.writeUTF("exit");
                            System.out.println("Disconnecting...");
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }
                }
            } else if (role.equals("customer")) {
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
                            dataOutputStream.writeUTF("pickupLocation: " + pickup + "\ndestination: " + dest);
                            System.out.println("Server: " + dataInputStream.readUTF());
                            break;
                        case 2:
                            dataOutputStream.writeUTF("view status");
                            System.out.println("Server: " + dataInputStream.readUTF());
                            break;
                        case 3:
                            dataOutputStream.writeUTF("exit");
                            System.out.println("Disconnecting...");
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }
                }
            } else if (role.equals("admin")) {
                System.out.println("Admin menu coming soon...");
            }

            scanner.close();
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
