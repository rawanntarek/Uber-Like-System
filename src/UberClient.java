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

            if(role.equals("driver"))
            {
                System.out.println("Driver Menu:");
                System.out.println("1. Offer a fare for a ride request");
                System.out.println("2. Send status updates of the ride (start/end)");
                System.out.println("3. Disconnect from the server.");
                System.out.print("Choose an option: ");
            } else if (role.equals("customer")) {
                System.out.println("\nCustomer Menu:");
                System.out.println("1. Request a ride");
                System.out.println("2. View ride status");
                System.out.println("3. Rate your last ride");
                System.out.println("4. Disconnect from server.");
                System.out.print("Choose an option: ");
            }

            if (role.equals("driver")) {

                // Single thread for listening to server messages
                new Thread(() -> {
                    try {
                        while (true) {
                            String serverMsg = input.readUTF();
                            if(serverMsg.equals("exit")) {
                                System.out.println("Disconnected from server.");
                                socket.close();
                                break;
                            }
                            System.out.println("---------------------------------------");
                            System.out.println(serverMsg);
                            System.out.println("---------------------------------------");

                            System.out.println("Driver Menu:");
                            System.out.println("1. Offer a fare for a ride request");
                            System.out.println("2. Send status updates of the ride (start/end)");
                            System.out.println("3. Disconnect from the server.");
                            System.out.println("Choose an option: ");
                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected from server.");
                    }
                }).start();

               int choice = 0;
                while (true) {
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
                            String serverResponse = input.readUTF();
                            if (serverResponse.equals("exit")) {
                                System.out.println("Disconnected from server.");
                                socket.close();
                                return;
                            } else {
                                System.out.println(serverResponse);
                            }
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }
                }
            }
            else if (role.equals("customer")) {
                new Thread(() -> {
                    try {
                        while (true) {
                            String msg = input.readUTF();
                            System.out.println("---------------------------------------");
                            System.out.println(msg);
                            System.out.println("---------------------------------------");
                            if(msg.equals("exit")) {
                                System.out.println("Disconnected from server.");
                                socket.close();
                                break;
                            }
                            if(!msg.startsWith("Offer:")&&!msg.startsWith("Do you want")) {
                                System.out.println("Customer Menu:");
                                System.out.println("1. Request a ride");
                                System.out.println("2. View ride status");
                                System.out.println("3. Rate your last ride");
                                System.out.println("4. Disconnect from server.");
                                System.out.println("Choose an option: ");
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected from server.");
                    }
                }).start();

                int choice = 0;
                while (choice != 4) {
                    choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            System.out.print("Enter Pickup Location: ");
                            String pickup = scanner.nextLine();
                            System.out.print("Enter Destination: ");
                            String dest = scanner.nextLine();
                            output.writeUTF("pickupLocation: " + pickup + "\ndestination: " + dest);
                            while(true)
                            {
                                String offer=input.readUTF();
                                System.out.println(offer);
                                if (offer.startsWith("Offer:")) {
                                    System.out.print("Do you want to accept this offer? (yes/no): ");
                                    String answer = scanner.nextLine();
                                    if(answer.equalsIgnoreCase("yes"))
                                    {
                                        output.writeUTF("acceptOffer");
                                    } else if (answer.equalsIgnoreCase("no")) {
                                        output.writeUTF("declineOffer");
                                    }
                                }
                                else {
                                    break;
                                }
                            }
                            break;
                        case 2:
                            output.writeUTF("viewStatus");
                            break;
                        case 3:
                            System.out.print("Enter overall rating (1-5): ");
                            double overall = scanner.nextDouble();
                            System.out.print("Enter driving skill rating (1-5): ");
                            double drivingSkill = scanner.nextDouble();
                            System.out.print("Enter good music rating (1-5): ");
                            double music = scanner.nextDouble();
                            System.out.print("Enter friendliness rating (1-5): ");
                            double friendliness = scanner.nextDouble();
                            scanner.nextLine();

                            if (overall >= 1 && overall <= 5 &&
                                    drivingSkill >= 1 && drivingSkill <= 5 &&
                                    music >= 1 && music <= 5 &&
                                    friendliness >= 1 && friendliness <= 5) {
                                output.writeUTF("rate:" + overall + "," + drivingSkill + "," + music + "," + friendliness);
                            } else {
                                System.out.println("rating must be between 1 and 5.");
                            }

                            break;
                        case 4:
                            output.writeUTF("exit");
                            String serverResponse = input.readUTF();
                            if (serverResponse.equals("exit")) {
                                System.out.println("Disconnected from server.");
                                socket.close();
                                return;
                            } else {
                                System.out.println(serverResponse);
                            }
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }
                }
            }
            else if (role.equals("admin")) {
                int choice = 0;

                while (choice != 2) {
                    System.out.println("Admin Menu:");
                    System.out.println("1. View Statistics");
                    System.out.println("2. Disconnect from server.");
                    System.out.println("Choose an option: ");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    switch (choice) {
                        case 1:
                            output.writeUTF("viewStats");
                            while (true) {
                                String msg = input.readUTF();
                                if (msg.equals("end")) {
                                    break;
                                }
                                System.out.println(msg);
                            }
                            break;
                        case 2:
                            output.writeUTF("exit");
                            break;
                    }
                }
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