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
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            String role = "";
            String username;

            output.writeUTF("Do you want to login or register?");
            String action = input.readUTF();

            if (action.equalsIgnoreCase("register")) {
                output.writeUTF("Enter username:");
                username = input.readUTF();

                output.writeUTF("Enter password:");
                String password = input.readUTF();

                output.writeUTF("Are you a customer or driver?");
                role = input.readUTF().toLowerCase();

                synchronized (UberServer.users) {
                    boolean exists = UberServer.users.stream()
                            .anyMatch(u -> u.getUsername().equals(username));
                    if (exists) {
                        output.writeUTF("Username already taken. Disconnecting...");
                        return;
                    }
                    UberServer.users.add(new User(username, password, role));
                }

                output.writeUTF("Registered successfully as " + role + ".");
            } else if (action.equalsIgnoreCase("login")) {
                output.writeUTF("Enter username:");
                username = input.readUTF();

                output.writeUTF("Enter password:");
                String password = input.readUTF();

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
                    output.writeUTF("Invalid username or password. Disconnecting...");
                    return;
                } else {
                    output.writeUTF("Login successful as " + role + ".");
                }
            } else {
                username = "";
                output.writeUTF("Invalid action. Disconnecting...");
                return;
            }

            String address = socket.getInetAddress().toString();

            if (role.equals("customer")) {
                int id = UberServer.addCustomer(address, username, output);

                System.out.println("Customer connected with ID: " + id);
                FeatureHandler.handleCustomerFeatures(input, output, username, id);

            } else if (role.equals("driver")) {
                int id = UberServer.addDriver(address, username, output);
                System.out.println("Driver connected with ID: " + id);
                FeatureHandler.handleDriverFeatures(input, output, username, id);

            } else if (role.equals("admin")) {
                FeatureHandler.handleAdminFeatures(output);
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
