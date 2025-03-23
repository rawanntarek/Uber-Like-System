import java.io.*;
import java.net.Socket;
import java.util.Scanner;
//push
public class UberClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 6660);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));

            System.out.println(input.readUTF());
            String action = scanner.readLine();
            output.writeUTF(action);
            System.out.print(input.readUTF());
            String username = scanner.readLine();
            output.writeUTF(username);

            System.out.print(input.readUTF());
            String password = scanner.readLine();
            output.writeUTF(password);

            if (action.equalsIgnoreCase("register")) {
                System.out.print(input.readUTF());
                String role = scanner.readLine();
                output.writeUTF(role);

            }

            String response = input.readUTF();
            System.out.println("---------------------------------------");
            System.out.println("Server: " + response);
            System.out.println("---------------------------------------");

            if (response.contains("Disconnecting")) return;
            String[] tokens = response.toLowerCase().replace(".", "").split(" ");
            String role = tokens[tokens.length - 1];

            if(role.equals("driver"))
            {
                System.out.println("Driver Menu:");
                System.out.println("1. Offer a fare for a ride request");
                System.out.println("2. Send status updates of the ride (start/end)");
                System.out.println("3. Disconnect from the server.");
                System.out.print("Choose an option: ");
            }

            if (role.equals("driver")) {
                new Thread(() -> {
                    try {
                        while (true) {
                            String serverMsg = input.readUTF();
                            System.out.println("\n"+serverMsg);

                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected from server.");
                    }
                }).start();
                System.out.println("Listening for incoming messages...");


                int choice = 0;
                while (choice != 3) {

                    choice = Integer.parseInt(scanner.readLine());
                
                    scanner.readLine();

                    switch (choice) {
                        case 1:
                            System.out.print("Offer a fare: ");
                            int fare = Integer.parseInt(scanner.readLine());
                            output.writeUTF("fare: " + fare);
                            break;
                        case 2:
                            System.out.print("Enter ride status (start/end): ");
                            String status = scanner.readLine();
                            output.writeUTF(status);
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
                            System.out.println("---------------------------------------");
                            System.out.println(msg);
                            System.out.println("---------------------------------------");
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
                    System.out.println("Choose an option: ");

                    choice = Integer.parseInt(scanner.readLine());
                    scanner.readLine();

                    switch (choice) {
                        case 1:
                            System.out.print("Enter Pickup Location: ");
                            String pickup = scanner.readLine();
                            System.out.print("Enter Destination: ");
                            String dest = scanner.readLine();
                            output.writeUTF("pickupLocation: " + pickup + "\ndestination: " + dest);
                            while(true)
                            {
                                String offer=input.readUTF();
                                System.out.println(offer);
                                if (offer.startsWith("Offer:")) {
                                    System.out.print("Do you want to accept this offer? (yes/no): ");
                                    String answer = scanner.readLine();
                                    if(answer.equalsIgnoreCase("yes"))
                                    {
                                        output.writeUTF("acceptOffer");
                                    } else if (answer.equalsIgnoreCase("no")) {
                                        output.writeUTF("declineOffer");
                                    }
                                }
                                else {
                                    System.out.println("---------------------------------------");
                                    System.out.println(offer);
                                    System.out.println("---------------------------------------");
                                    break;
                                }
                            }

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
