import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
//customer features

public class Customer_Features {

    public static void handleCustomerFeatures(DataInputStream input, DataOutputStream output, String username, int id) throws IOException {
        while (true) {
            String message = input.readUTF();

            if (message.startsWith("pickupLocation")) {
                handleRideRequest(message, username, output);
            } else if (message.equals("acceptOffer")) {
                Accept_Offer(username, output);
                
            } else if (message.equals("declineOffer")) {
                declineOffer(username, output);
            } else if (message.equals("viewStatus")) {
                viewRideStatus(username, output);
            } else if (message.equals("exit")) {
                if(disconnect(username))
                {
                    output.writeUTF("exit");
                    break;
                }
                else {
                    output.writeUTF("cannot disconnect your ride is on going");
                }
            } else {
                output.writeUTF("Unknown command.");
                System.out.println("Unknown customer message: " + message);
            }
        }
    }

    private static void handleRideRequest(String message, String username, DataOutputStream output) throws IOException {
        String[] parts = message.split("\n");
        String pickup = parts[0].split(": ")[1];
        String destination = parts[1].split(": ")[1];

        int rideId = UberServer.rideIdCounter++;
        Ride ride = new Ride(rideId, username, pickup, destination);
        UberServer.rides.add(ride);

        output.writeUTF("Ride request sent. Ride ID: " + rideId);

        for (Map.Entry<String, DataOutputStream> entry : UberServer.driverOutputs.entrySet()) {
            try {
                DataOutputStream driverOut = entry.getValue();
                driverOut.writeUTF("New ride request from " + username + ": Pickup at " + pickup + ", Destination: " + destination + " (Ride ID: " + rideId + ")");
                System.out.println("Broadcasted to driver: " + entry.getKey());
                Thread.sleep(50);
            } catch (IOException e) {
                System.out.println("Failed to notify driver: " + entry.getKey());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void Accept_Offer(String username, DataOutputStream output) throws IOException {
        Offer offer = UberServer.pendingCustomerOffers.get(username);

        if (offer == null) {
            output.writeUTF(" No  offers to accept.");
            return;
        }

        Ride rideToAssign = null;
        for (Ride r : UberServer.rides) {
            if (r.getRideId() == offer.getRideId() && r.getCustomerUsername().equals(username)) {
                rideToAssign = r;
                break;
            }
        }

        if (rideToAssign == null || rideToAssign.getAssignedDriver() != null) {
            output.writeUTF("Ride not found or already assigned.");
            return;
        }

        rideToAssign.setAssignedDriver(offer.getName());
        rideToAssign.setStatus("assigned");
        UberServer.driverAvailability.put(offer.getName(), false);

        output.writeUTF("You accepted the offer from " + offer.getName() +
                " for Ride ID: " + offer.getRideId() + " (Fare: " + offer.getFare() + ")");

        DataOutputStream driverOut = UberServer.driverOutputs.get(offer.getName());
        if (driverOut != null) {
            driverOut.writeUTF("Your offer was accepted by customer " + username +
                    " for Ride ID: " + offer.getRideId() + " (Fare: " + offer.getFare() + ")");
        }

        UberServer.pendingCustomerOffers.remove(username);
    }
    public static void declineOffer(String username, DataOutputStream output) throws IOException {
        Offer declined = UberServer.pendingCustomerOffers.remove(username);
        UberServer.driverAvailability.put(declined.getName(), true);

        if (declined == null) {
            output.writeUTF("No offer to decline.");
            return;
        }

        Ride ride = null;
        for (Ride r : UberServer.rides) {
            if (r.getRideId() == declined.getRideId()
                    && r.getCustomerUsername().equals(username)
                    && r.getStatus().equals("pending")) {
                ride = r;
                break;
            }
        }

        if (ride == null) {
            output.writeUTF("Ride not found.");
            return;
        }

        ride.getFareOffers().remove(declined.getName());

        String nextDriver = null;
        for (String driver : ride.getFareOffers().keySet()) {
            nextDriver = driver;
            break;
        }

        if (nextDriver != null) {
            int nextFare = ride.getFareOffers().get(nextDriver);
            Offer next = new Offer(nextDriver, ride.getRideId(), nextFare);
            UberServer.pendingCustomerOffers.put(username, next);

            output.writeUTF("Offer: " + next.getName() +
                    " offered " + next.getFare() +
                    " for your ride (Ride ID: " + next.getRideId() + ")");
        } else {
            output.writeUTF("No more offers available for your ride.");
        }
    }
    private static void viewRideStatus(String username, DataOutputStream output) throws IOException {
        Ride latestRide = null;

        for (int i = UberServer.rides.size() - 1; i >= 0; i--) {
            Ride r = UberServer.rides.get(i);
            if (r.getCustomerUsername().equals(username)) {
                latestRide = r;
                break;
            }
        }

        if (latestRide == null) {
            output.writeUTF("You have not requested any rides yet.");
            return;
        }

        String status = latestRide.getStatus();
        String response = "Your ride (Ride ID: " + latestRide.getRideId() + ") is currently '" + status + "',"+" Assigned driver: " + latestRide.getAssignedDriver();



        output.writeUTF(response);
    }
    private static boolean disconnect(String username) {
        for (Ride r : UberServer.rides) {
            if (r.getCustomerUsername().equals(username)) {
                String status = r.getStatus();
                if (status.equals("assigned") || status.equals("in progress")) {
                    return false;
                }
            }
        }
        return true;
    }




}
