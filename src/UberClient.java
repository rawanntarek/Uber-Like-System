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

            String[] tokens = response.toLowerCase().replace(".", "").split(" ");
            String role = tokens[tokens.length - 1];

            // Separate Thread to Listen for Server Messages
            new Thread(() -> {
                try {
                    while (true) {
                        String serverMsg = input.readUTF();
                        System.out.println("\n---------------------------------------");
                        System.out.println(serverMsg);
                        System.out.println("---------------------------------------");
                        System.out.print("\nChoose an option: ");
                    }
                } catch (IOException e) {
                    System.out.println("\nConnection Closed.");
                }
            }).start();

            // Main Thread Handles User Input
            if (role.equals("customer")) {
                int choice = 0;
                while (choice != 3) {
                    System.out.println("\nCustomer Menu:");
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

                            while (true) {
                                String answer = scanner.nextLine();
                                output.writeUTF(answer);
                                if (answer.equalsIgnoreCase("acceptOffer")) {
                                    break; // break after accepting offer
                                }
                            }
                            break;

                        case 2:
                            output.writeUTF("viewStatus");
                            break;

                        case 3:
                            output.writeUTF("exit");
                            System.out.println("Disconnecting...");
                            socket.close();
                            return;

                        default:
                            System.out.println("Invalid choice.");
                    }
                }
            } else if (role.equals("driver")) {
                int choice = 0;
                while (choice != 3) {
                    System.out.println("\nDriver Menu:");
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
                            break;

                        case 2:
                            System.out.print("Enter ride status (start/end): ");
                            String status = scanner.nextLine();
                            output.writeUTF(status);
                            break;

                        case 3:
                            output.writeUTF("exit");
                            System.out.println("Disconnecting...");
                            socket.close();
                            return;

                        default:
                            System.out.println("Invalid choice.");
                    }
                }
            } else if (role.equals("admin")) {
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
