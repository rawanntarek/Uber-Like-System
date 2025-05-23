import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class UberClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 6660);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);
            AtomicBoolean offerPending = new AtomicBoolean(false);

            System.out.println(input.readUTF());
            String action = scanner.nextLine();
            output.writeUTF(action);
            System.out.print(input.readUTF());
            String username = "";
            while(true) {
                username = scanner.nextLine();
                if(username.isEmpty()||username.length()<4) {
                    System.out.println("Empty username or username less than 4 characters please retry");
                }
                else {
                    break;
                }
            }
            output.writeUTF(username);

            System.out.print(input.readUTF());
            String password = "";
            while(true) {
                password = scanner.nextLine();
                if(password.isEmpty()||password.length()<6) {
                    System.out.println("Empty password or password less than 6 characters please retry");
                }
                else {
                    break;
                }
            }
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
                System.out.println("Choose an option: ");
            } else if (role.equals("customer")) {
                System.out.println("\nCustomer Menu:");
                System.out.println("1. Request a ride");
                System.out.println("2. View ride status");
                System.out.println("3. Rate your last ride");
                System.out.println("4. Disconnect from server.");
                System.out.println("Choose an option: ");
            }

            if (role.equals("driver")) {
                Thread listenerThread = new Thread(() -> {
                    try {
                        while (true) {
                            String serverMsg = input.readUTF();
                            System.out.println("---------------------------------------");
                            System.out.println(serverMsg);
                            System.out.println("---------------------------------------");

                            if(serverMsg.equals("exit")) {
                                System.out.println("Disconnected from server.");
                                socket.close();
                                System.exit(0);
                                break;
                            }

                            // Only reprint menu if we're not in the middle of a fare offer process
                            if (!serverMsg.startsWith("Available pending rides:") && !serverMsg.startsWith("Enter the number")) {
                                System.out.println("Driver Menu:");
                                System.out.println("1. Offer a fare for a ride request");
                                System.out.println("2. Send status updates of the ride (start/end)");
                                System.out.println("3. Disconnect from the server.");
                                System.out.println("Choose an option: ");
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected from server.");
                    }
                });
                listenerThread.start();

                while (true) {
                    int choice = 0;
                    String Choice = scanner.nextLine();

                    try {
                        choice = Integer.parseInt(Choice);}
                    catch (NumberFormatException e) {
                        System.out.println("Invalid please enter a number");
                        continue;
                    }
                    try {
                        switch (choice) {
                            case 1:
                                output.writeUTF("fare: 0"); // Initial request to get list of rides
                                
                                // Wait for user input without menu interference
                                System.out.println("Enter the number of the ride you want to offer a fare for: ");
                                int rideChoice = scanner.nextInt();
                                scanner.nextLine();
                                
                                System.out.println("Enter the fare amount: ");
                                int fare = scanner.nextInt();
                                scanner.nextLine();
                                
                                output.writeUTF("fare: " + rideChoice + " " + fare);
                                break;
                            case 2:
                                System.out.println("Enter ride status (start/end): ");
                                String status ="";
                                while(true)
                                {
                                    status = scanner.nextLine();
                                    if(!status.equals("end")&&!status.equals("start"))
                                    {
                                        System.out.println("Invalid status please enter start or end");
                                    }
                                    else {
                                        break;
                                    }

                                }
                                output.writeUTF(status);
                                break;
                            case 3:
                                output.writeUTF("exit");
                                String serverResponse = input.readUTF();
                                if (serverResponse.equals("exit")) {
                                    System.out.println("Disconnected from server.");
                                    socket.close();
                                    System.exit(0);
                                } else {
                                    System.out.println(serverResponse);
                                }
                                break;
                            default:
                                System.out.println("Invalid choice , please enter 1 or 2 or 3");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
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
                                System.out.println("Disconnected from server ....");
                                socket.close();
                                System.exit(0);
                                break;
                            }
                            if(!msg.startsWith("Offer:")) {
                                System.out.println("Customer Menu:");
                                System.out.println("1. Request a ride");
                                System.out.println("2. View ride status");
                                System.out.println("3. Rate your last ride");
                                System.out.println("4. Disconnect from server.");
                                System.out.println("Choose an option: ");
                            }
                            else
                            {
                                offerPending.set(true);
                                System.out.println("Do you want to accept this offer? (yes/no): ");

                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected from server catch.");
                    }
                }).start();
                int choice=0;
                while (choice != 4) {
                    String Input=scanner.nextLine();
                    if(offerPending.get()) {
                        if (Input.equalsIgnoreCase("yes")) {
                            output.writeUTF("acceptOffer");
                        } else if (Input.equalsIgnoreCase("no")) {
                            output.writeUTF("declineOffer");
                        }
                        else
                        {
                            System.out.println("Invalid choice , please enter yes or no");
                            continue;
                        }
                        offerPending.set(false);
                        continue;
                    }
                    try {
                        choice = Integer.parseInt(Input);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid please enter a number");
                        continue;
                    }
                    try {
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
                                    System.out.println("Disconnected from server in case 4.");
                                    socket.close();
                                    System.exit(0);
                                } else {
                                    System.out.println(serverResponse);
                                }
                                break;
                            default:
                                System.out.println("Invalid choice please enter 1 or 2 or 3 or 4");
                        }
                    }catch (Exception e) {
                        System.out.println(e.getMessage());
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
                    String Choice = scanner.nextLine();
                    try {
                        choice = Integer.parseInt(Choice);}
                    catch (NumberFormatException e) {
                        System.out.println("Invalid please enter a number");
                        continue;
                    }
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
                        default:
                            System.out.println("Invalid choice please enter 1 or 2");

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