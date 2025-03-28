import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//driver features

public class Driver_features {

    public static void handleDriverFeatures(DataInputStream input, DataOutputStream output, String username, int id) throws IOException {
        while (true) {
            String message = input.readUTF();

            if (message.startsWith("fare:")) {
                handleFareOffer(message, username, output);
            } else if (message.equals("start") || message.equals("end")) {
                updateRideStatus(username,message, output);
            } else if (message.equals("exit")) {
              
                boolean hasActiveRide = false;
                for (Ride r : UberServer.rides) {
                    if (username.equals(r.getAssignedDriver()) && 
                        (r.getStatus().equals("in progress") || r.getStatus().equals("assigned"))) {
                        hasActiveRide = true;
                        break;
                    }
                }
                
                if (hasActiveRide) {
                    output.writeUTF("Cannot disconnect while you have an active ride. Please complete the ride first.");
                    continue;
                }
                
                
                UberServer.driverAvailability.put(username, true);
                UberServer.driverOutputs.remove(username);
                
                for (Ride r : UberServer.rides) {
                    if (r.getStatus().equals("pending")) {
                        r.getFareOffers().remove(username);
                    }
                }
                
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

        // Get all pending rides
        List<Ride> pendingRides = new ArrayList<>();
        for (Ride r : UberServer.rides) {
            if (r.getStatus().equals("pending")) {
                pendingRides.add(r);
            }
        }

        if (pendingRides.isEmpty()) {
            output.writeUTF("No available rides to offer a fare for.");
            return;
        }

        // If this is the initial request (message contains only "fare: 0")
        if (message.equals("fare: 0")) {
            // Send list of pending rides to driver
            StringBuilder ridesList = new StringBuilder();
            ridesList.append("Available pending rides:\n");
            for (int i = 0; i < pendingRides.size(); i++) {
                Ride r = pendingRides.get(i);
                ridesList.append(i + 1).append(". Ride ID: ").append(r.getRideId())
                        .append(" - From: ").append(r.getPickup())
                        .append(" To: ").append(r.getDestination())
                        .append("\n");
            }
            ridesList.append("Enter the number of the ride you want to offer a fare for: ");
            output.writeUTF(ridesList.toString());
            return;
        }

        // Handle the fare offer with ride selection
        try {
            String[] parts = message.split(" ");
            if (parts.length != 3) {
                output.writeUTF("Invalid message format. Please try again.");
                return;
            }

            int rideIndex = Integer.parseInt(parts[1]) - 1;
            int fare = Integer.parseInt(parts[2]);

            if (rideIndex < 0 || rideIndex >= pendingRides.size()) {
                output.writeUTF("Invalid ride selection.");
                return;
            }

            Ride selectedRide = pendingRides.get(rideIndex);
            String customerUsername = selectedRide.getCustomerUsername();

            selectedRide.addFareOffer(username, fare);
            UberServer.driverAvailability.put(username, false);

            output.writeUTF("Offer:" + fare + " sent for ride ID: " + selectedRide.getRideId());
            System.out.println("Offer:" + username + " offered fare " + fare + " for Ride " + selectedRide.getRideId());
            if (!UberServer.pendingCustomerOffers.containsKey(customerUsername) && selectedRide.getAssignedDriver()==null) {
                Offer offer = new Offer(username, selectedRide.getRideId(), fare);
                UberServer.pendingCustomerOffers.put(customerUsername, offer);
                DataOutputStream customerOut = UberServer.customerOutputs.get(customerUsername);
                if (customerOut != null) {
                    try {
                        double rating = 5.0;
                        for (ClientInfo driver : UberServer.drivers) {
                            if (driver.getUsername().equals(username)) {
                                if (driver.getRating() != 0.0) {
                                    rating = driver.getRating();
                                }
                            }
                        }
                        String offerMessage = "Offer:" + username + " Rating: " + rating + " offered " + fare +
                                " for your ride (Ride ID: " + selectedRide.getRideId() + ")";
                        customerOut.writeUTF(offerMessage);
                        System.out.println("Sent offer to customer: " + customerUsername);
                    } catch (IOException e) {
                        System.out.println("Failed to notify customer: " + customerUsername + " - " + e.getMessage());
                    }
                } else {
                    System.out.println("Customer connection not found: " + customerUsername);
                }
            }
        } catch (NumberFormatException e) {
            output.writeUTF("Invalid input. Please enter valid numbers for ride selection and fare amount.");
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
                    customerOut.writeUTF("Your can rate your driver from option 3 ");
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
