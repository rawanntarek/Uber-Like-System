import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class FeatureHandler {

    // ------------------ CUSTOMER ------------------
    public static void handleCustomerFeatures(DataInputStream input, DataOutputStream output, String username, int id) throws IOException {
        while (true) {
            String message = input.readUTF();

            if (message.startsWith("pickupLocation")) {
                String[] parts = message.split("\n");
                String pickup = parts[0].split(": ")[1];
                String destination = parts[1].split(": ")[1];

                int rideId = UberServer.rideIdCounter++;
                Ride ride = new Ride(rideId, username, pickup, destination);
                UberServer.rides.add(ride);

                output.writeUTF("Ride request sent. Ride ID: " + rideId);

                // Broadcast to available drivers
                synchronized (UberServer.driverOutputs) {
                    for (Map.Entry<String, DataOutputStream> entry : UberServer.driverOutputs.entrySet()) {
                        try {
                            DataOutputStream driverOut = entry.getValue();
                            driverOut.writeUTF("New ride request from " + username + ": Pickup at " + pickup + ", Destination: " + destination + " (Ride ID: " + rideId + ")");
                            System.out.println("Broadcasted to driver: " + entry.getKey());
                        } catch (IOException e) {
                            System.out.println("Failed to notify driver: " + entry.getKey());
                        }
                    }
                }

            } else if (message.equals("viewStatus")) {
                output.writeUTF("View status feature not implemented yet.");

            } else if (message.equals("exit")) {
                output.writeUTF("exit");
                break;

            } else {
                output.writeUTF("Unknown command.");
                System.out.println("Unknown customer message: " + message);
            }
        }
    }

    // ------------------ DRIVER ------------------
    public static void handleDriverFeatures(DataInputStream input, DataOutputStream output, String username, int id) throws IOException {
        while (true) {
            String message = input.readUTF();

            if (message.startsWith("fare:")) {
                output.writeUTF("fare");
                // Placeholder for fare offer logic

            } else if (message.equals("start") || message.equals("end")) {
                output.writeUTF("ride");
                // Placeholder for ride status logic

            } else if (message.equals("exit")) {
                output.writeUTF("exit");
                break;

            } else {
                output.writeUTF("Unknown command.");
                System.out.println("Unknown driver message: " + message);
            }
        }
    }

    // ------------------ ADMIN ------------------
    public static void handleAdminFeatures(DataOutputStream output) throws IOException {
        output.writeUTF("Admin features coming soon.");
    }
}
