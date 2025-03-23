import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Driver_features {

    public static void handleDriverFeatures(DataInputStream input, DataOutputStream output, String username, int id) throws IOException {
        while (true) {
            String message = input.readUTF();

            if (message.startsWith("fare:")) {
                handleFareOffer(message, username, output);
            } else if (message.equals("start") || message.equals("end")) {
                updateRideStatus(username,message, output);
            } else if (message.equals("exit")) {
                output.writeUTF("exit");
                break;
            } else {
                output.writeUTF("Unknown command.");
                System.out.println("Unknown driver message: " + message);
            }
        }
    }

    private static void handleFareOffer(String message, String username, DataOutputStream output) throws IOException {
        if (!UberServer.driverAvailability.get(username)) {
            output.writeUTF("Not applicable");
            return;
        }

        Ride latestRide = null;
        for (int i = UberServer.rides.size() - 1; i >= 0; i--) {
            Ride r = UberServer.rides.get(i);
            if (r.getStatus().equals("pending")) {
                latestRide = r;
                break;
            }
        }

        if (latestRide == null) {
            output.writeUTF("No available ride to offer a fare for.");
        } else {
            int fare = Integer.parseInt(message.split(":")[1].trim());
            latestRide.addFareOffer(username, fare);
            UberServer.driverAvailability.put(username, false);

            output.writeUTF("Offer:" + fare + " sent for ride ID: " + latestRide.getRideId());
            Offer offer = new Offer(username, latestRide.getRideId(), fare);
           


            System.out.println("Offer:" + username + " offered fare " + fare + " for Ride " + latestRide.getRideId());

            String customerUsername = latestRide.getCustomerUsername();
            DataOutputStream customerOut = UberServer.customerOutputs.get(customerUsername);
            if (customerOut != null) {
                try {
                    customerOut.writeUTF("Offer:" + username + " offered " + fare +
                            " for your ride (Ride ID: " + latestRide.getRideId() + ")");
                } catch (IOException e) {
                    System.out.println("Failed to notify customer: " + customerUsername);
                }
            }
        }
    }
    private static void updateRideStatus(String driverUsername, String status, DataOutputStream output) throws IOException {
        boolean found = false;

        for (Ride r : UberServer.rides) {
            if (driverUsername.equals(r.getAssignedDriver()) &&
                    ((status.equals("start") && r.getStatus().equals("assigned")) ||
                            (status.equals("end") && r.getStatus().equals("in progress")))) {

                if (status.equals("start")) {
                    r.setStatus("in progress");
                    output.writeUTF("Ride started for Ride ID: " + r.getRideId());
                    DataOutputStream customerOut = UberServer.customerOutputs.get(r.getCustomerUsername());
                    customerOut.writeUTF("Your ride started with "+driverUsername);



                } else if (status.equals("end")) {
                    r.setStatus("completed");
                    output.writeUTF("Ride completed for Ride ID: " + r.getRideId());
                    DataOutputStream customerOut = UberServer.customerOutputs.get(r.getCustomerUsername());
                    customerOut.writeUTF("Your ride completed with "+driverUsername);
                    UberServer.driverAvailability.put(driverUsername, true);
                }

                found = true;
                break;
            }
        }

        if (!found) {
            output.writeUTF("No assigned ride found to update");
        }
    }

}
