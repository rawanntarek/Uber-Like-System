// client handler
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
            String password;


            String address = socket.getInetAddress().toString();

            output.writeUTF("Do you want to login or register?");
            String action = input.readUTF();

            if (action.equals("register")) {
                output.writeUTF("Enter username:");
                username = input.readUTF();

                output.writeUTF("Enter password:");
                password = input.readUTF();

                output.writeUTF("Are you a customer or driver?");
                role = input.readUTF().toLowerCase();


                boolean exists = false;
                if(role.equals("customer"))
                {
                    for(ClientInfo customer:UberServer.customers)
                    {
                        if(customer.getUsername().equals(username))
                        {
                            exists = true;
                            break;
                        }
                    }
                }
                else if(role.equals("driver"))
                {
                    for(ClientInfo driver:UberServer.drivers)
                    {
                        if(driver.getUsername().equals(username))
                        {
                            exists = true;
                            break;
                        }
                    }
                }

                if (exists) {
                    output.writeUTF("Username already taken. Disconnecting...");
                    return;
                }
                int id;
                if (role.equals("customer")) {
                    id=UberServer.addCustomer(address, username, password, output);
                    System.out.println("Customer registered with ID: " + id);
                    output.writeUTF("Registered successfully as customer.");
                    Customer_Features.handleCustomerFeatures(input, output, username, id);
                } else if (role.equals("driver")) {
                    id=UberServer.addDriver(address, username, password, output);
                    System.out.println("Driver registered with ID: " + id);
                    output.writeUTF("Registered successfully as driver.");
                    Driver_features.handleDriverFeatures(input, output, username, id);

                }
                output.writeUTF("Registered successfully as " + role + ".");

            } else if (action.equals("login")) {
                output.writeUTF("Enter username:");
                username = input.readUTF();

                output.writeUTF("Enter password:");
                 password = input.readUTF();

                ClientInfo user=null;
                for (ClientInfo customer : UberServer.customers) {
                    if (customer.getUsername().equals(username) && customer.getPassword().equals(password)) {
                        user = customer;
                        role = "customer";
                        break;
                    }
                }
                if(user==null)
                {
                    for (ClientInfo driver : UberServer.drivers) {
                        if (driver.getUsername().equals(username) && driver.getPassword().equals(password)) {
                            user = driver;
                            role = "driver";
                            break;
                        }
                    }
                }



                if (user == null && username.equals("admin") && password.equals("admin123")) {
                    role = "admin";
                    username = "admin";
                } else if (user == null) {
                    output.writeUTF("Invalid username or password. Disconnecting...");
                    return;
                }

                // Update customer's connection if they're logging in
                if (role.equals("customer")) {
                    // Clear any old pending offers for this customer
                    UberServer.pendingCustomerOffers.remove(username);
                    // Update the customer's connection
                    UberServer.customerOutputs.put(username, output);
                    System.out.println("Updated customer connection for: " + username);
                }


                output.writeUTF("Login successful as " + role);
            } else {
                output.writeUTF("Invalid action. Disconnecting...");
                return;
            }

            if (role.equals("admin")) {
                System.out.println("Admin Connected");
                Admin_features.handleAdminFeatures(input,output);
            }
            else if (role.equals("customer")) {
                System.out.println("Customer Logged in");
                Customer_Features.handleCustomerFeatures(input, output, username, -1);

            } else if (role.equals("driver")) {
                System.out.println("driver Logged in");
                Driver_features.handleDriverFeatures(input, output, username, -1);

            }

            System.out.println("Client disconnected.");

        } catch (IOException e) {
            System.out.println("client disconnected unexpectedly");

        }
        catch(Exception e)
        {
            System.out.println("Unexpected exception: " + e.getMessage());
        }finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket");
            }
        }
    }
}
