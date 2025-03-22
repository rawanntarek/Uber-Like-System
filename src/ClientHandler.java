import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String role = "";
            String username;

            dataOutputStream.writeUTF("Do you want to login or register?");
            String action = dataInputStream.readUTF();

            if (action.equalsIgnoreCase("register")) {
                dataOutputStream.writeUTF("Enter username:");
                username = dataInputStream.readUTF();

                dataOutputStream.writeUTF("Enter password:");
                String password = dataInputStream.readUTF();

                dataOutputStream.writeUTF("Are you a customer or driver?");
                role = dataInputStream.readUTF().toLowerCase();

                synchronized (UberServer.users) {
                    boolean exists = UberServer.users.stream()
                            .anyMatch(u -> u.getUsername().equals(username));
                    if (exists) {
                        dataOutputStream.writeUTF("Username already taken. Disconnecting...");
                        return;
                    }
                    UberServer.users.add(new User(username, password, role));
                }

                dataOutputStream.writeUTF("Registered successfully as " + role + ".");
            } else if (action.equalsIgnoreCase("login")) {
                dataOutputStream.writeUTF("Enter username:");
                username = dataInputStream.readUTF();

                dataOutputStream.writeUTF("Enter password:");
                String password = dataInputStream.readUTF();

                User user = null;
                synchronized (UberServer.users) {
                    for (User u : UberServer.users) {
                        if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                            user = u;
                            role = u.getRole();
                            break;
                        }
                    }
                }

                if (user == null) {
                    dataOutputStream.writeUTF("Invalid username or password. Disconnecting...");
                    return;
                } else {
                    dataOutputStream.writeUTF("Login successful as " + role + ".");
                }
            } else {
                username = "";
                dataOutputStream.writeUTF("Invalid action. Disconnecting...");
                return;
            }

            String address = socket.getInetAddress().toString();

            if (role.equals("customer")) {
                int id = UberServer.addCustomer(address);
                System.out.println("Customer connected with ID: " + id);
                while (true) {
                    String received_message = dataInputStream.readUTF();
                    if (received_message.startsWith("pickupLocation")) {
                        dataOutputStream.writeUTF("pickup");
                    } else if (received_message.equals("viewStatus")) {
                        dataOutputStream.writeUTF("view");
                    } else if (received_message.equals("exit")) {
                        dataOutputStream.writeUTF("exit");
                        break;
                    } else {
                        System.out.println("Unknown customer command.");
                    }
                }

            } else if (role.equals("driver")) {
                int id = UberServer.addDriver(address);
                System.out.println("Driver connected with ID: " + id);
                while (true) {
                    String received_message = dataInputStream.readUTF();
                    if (received_message.startsWith("fare:")) {
                        dataOutputStream.writeUTF("fare");
                    } else if (received_message.equals("start") || received_message.equals("end")) {
                        dataOutputStream.writeUTF("ride");
                    } else if (received_message.equals("exit")) {
                        dataOutputStream.writeUTF("exit");
                        break;
                    } else {
                        System.out.println("Unknown driver command.");
                    }
                }

            } else if (role.equals("admin")) {
                dataOutputStream.writeUTF("Admin logged in. Features coming soon.");
                // You can add admin menu later
            }

            System.out.println("Client disconnected.");

        } catch (IOException e) {
            System.out.println("Error in client handler: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket");
            }
        }
    }
}
