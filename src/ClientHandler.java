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
            String role = dataInputStream.readUTF();
            String address = socket.getInetAddress().toString();

            if (role.equals("customer")) {
                int id=UberServer.addCustomer(address);
                System.out.println("Customer connected with ID: " + id);
                while(true)
                {
                    String recieved_message=dataInputStream.readUTF();
                    if(recieved_message.startsWith("pickupLocation"))
                    {
                        dataOutputStream.writeUTF("pickup");
                    } else if (recieved_message.equals("viewStatus")) {
                        dataOutputStream.writeUTF("view");
                    } else if (recieved_message.equals("exit")) {
                        dataOutputStream.writeUTF("exit");
                        break;
                    }
                    else
                    {
                        System.out.println("unknown");
                    }
                }


            } else if (role.equals("driver")) {
                int id = UberServer.addDriver(address);
                System.out.println("Driver connected with ID: " + id);
                String RideStatus ="";
                while (true) {
                    String recieved_message=dataInputStream.readUTF();
                    if(recieved_message.startsWith("fare:"))
                    {
                        dataOutputStream.writeUTF("fare");
                    }
                    else if (recieved_message.equals("start")||recieved_message.equals("end"))
                    {
                        dataOutputStream.writeUTF("ride");
                    }
                    else if (recieved_message.equals("exit")) {
                        dataOutputStream.writeUTF("exit");
                        break;
                    }
                    else
                    {
                        System.out.println("unknown");
                    }
//                    if (RideStatus.equals("start")) {
//                        System.out.println("Driver " + id + " has started a ride"); //de kda kda el thread gowa el server fa bttl3 ala elserver
//                        dataOutputStream.writeUTF("Ride started"); //btb3t melserver lelclient
//                    } else if (RideStatus.equals("end")) {
//                        System.out.println("Driver " + id + " has ended the ride");
//                        dataOutputStream.writeUTF("Ride ended");
//                    } else if (RideStatus.equals("exit")) {
//                        System.out.println("Driver " + id + " disconnected");
//                        dataOutputStream.writeUTF("Driver disconnected");
//                        break;
//                    } else {
//                        dataOutputStream.writeUTF("Unknown status. Use 'start', 'end', or 'exit'.");
//                    }
                }


            } else {
                System.out.println("Invalid role");
            }
            System.out.println("Server closing");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket");
            }
        }


    }
}
