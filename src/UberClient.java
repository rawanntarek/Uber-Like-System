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


            System.out.println("Enter your role : ");
            Scanner scanner = new Scanner(System.in);
            String role = scanner.nextLine().toLowerCase();
            dataOutputStream.writeUTF(role);
            if(role.equals("driver")) {
                int choice=0;
                while(choice!=3)
                {
                    System.out.println("Driver Menu : ");
                    System.out.println("1. Offer a fare for a ride request");
                    System.out.println("2. Send status updates of the ride they have been assigned to (start or\n" +
                            "finish ride)");
                    System.out.println("3. Disconnect from the server.");
                    System.out.println("Choose an option :");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    switch(choice)
                    {
                        case 1:
                            System.out.println("Offer a fare for a ride request");
                            int fare = scanner.nextInt();
                            dataOutputStream.writeUTF("fare: "+fare);
                            try {
                                String response = dataInputStream.readUTF();
                                System.out.println("Server: " + response);
                            } catch (IOException e) {
                                System.out.println("Server disconnected unexpectedly.");
                                break;
                            }
                            break;
                        case 2:
                            System.out.println("enter ride status(start/end): ");
                            String status = scanner.nextLine();
                            dataOutputStream.writeUTF(status);
                            try {
                                String response = dataInputStream.readUTF();
                                System.out.println("Server: " + response);
                            } catch (IOException e) {
                                System.out.println("Server disconnected unexpectedly.");
                                break;
                            }
                            break;
                        case 3:
                            dataOutputStream.writeUTF("exit");
                            System.out.println("Disconnect from the server.");
                            break;
                        default:
                            System.out.println("Invalid choice");
                            break;
                    }

                }

            }
            else if(role.equals("customer"))
            {
                int choice=0;
                while(choice!=3)
                {
                    System.out.println("Customer Menu : ");
                    System.out.println("1. Request a ride by entering a pickup location and destination.");
                    System.out.println("2. View the current status of the requested ride.");
                    System.out.println("3. Disconnect from the server.");
                    System.out.println("Choose an option :");
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    switch(choice)
                    {
                        case 1:
                            System.out.println("Enter Pickup Location : ");
                            String pickupLocation = scanner.nextLine();
                            System.out.println("Enter Destination : ");
                            String destination = scanner.nextLine();
                            dataOutputStream.writeUTF("pickupLocation: "+pickupLocation+"\ndestination: "+destination);
                            try {
                                String response = dataInputStream.readUTF();
                                System.out.println("Server: " + response);
                            } catch (IOException e) {
                                System.out.println("Server disconnected unexpectedly.");
                                break;
                            }

                            break;
                        case 2:
                            dataOutputStream.writeUTF("view status");
                            try {
                                String response = dataInputStream.readUTF();
                                System.out.println("Server: " + response);
                            } catch (IOException e) {
                                System.out.println("Server disconnected unexpectedly.");
                                break;
                            }
                            break;
                        case 3:
                            dataOutputStream.writeUTF("exit");
                            System.out.println("Disconnect from the server.");
                            break;
                        default:
                            System.out.println("Invalid choice");
                    }

                }
            }

            scanner.close();
            dataOutputStream.close();
            dataInputStream.close();
            socket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
