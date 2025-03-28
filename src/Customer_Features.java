import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

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
            } else if (message.startsWith("rate:")) {
                handleRating(message, username, output);
            } else if (message.equals("exit")) {
                if (isRideOngoing(username)) {
                    output.writeUTF("You cannot disconnect during an ongoing ride.");
                } else {
                    output.writeUTF("exit");
                    break;
                }
            }else {
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
        if (declined == null) {
            return;
        }

        UberServer.driverAvailability.put(declined.getName(), true);



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
        ride.addDeclinedDriver(declined.getName());

        String nextDriver = null;
        for (String driver : ride.getFareOffers().keySet()) {
            if(!ride.getDeclinedDrivers().contains(driver)) {
                nextDriver = driver;
                break;
            }
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

        if (status.equals("completed") && !latestRide.isRated()) {
            response += "\nYou can rate this ride using option 3 in the menu.";
        } else if (latestRide.isRated()) {
            response += "\nYou rated this ride: " + latestRide.getRating() + "/5";
        }

        output.writeUTF(response);
    }

    private static void handleRating(String message, String username, DataOutputStream output) throws IOException {
        Ride latestRide = null;
        for (int i = UberServer.rides.size() - 1; i >= 0; i--) {
            Ride r = UberServer.rides.get(i);
            if (r.getCustomerUsername().equals(username) && r.getStatus().equals("completed") && !r.isRated()) {
                latestRide = r;
                break;
            }
        }

        if (latestRide == null) {
            output.writeUTF("No completed ride found to rate.");
            return;
        }

        String[] parts = message.split(":")[1].trim().split(",");
        double rating = Double.parseDouble(parts[0]);
        double drivingSkill = Double.parseDouble(parts[1]);
        double goodmusic = Double.parseDouble(parts[2]);
        double friendliness = Double.parseDouble(parts[3]);


        String driverUsername = latestRide.getAssignedDriver();
        for (ClientInfo driver : UberServer.drivers) {
            if (driver.getUsername().equals(driverUsername)) {
                driver.addRating(rating,drivingSkill,friendliness,goodmusic);
                break;
            }
        }
        latestRide.setRated(true);

        output.writeUTF("Thank you for rating your ride with " + driverUsername + "!");

        DataOutputStream driverOut=UberServer.driverOutputs.get(driverUsername);
        if(driverOut!=null)
        {
            driverOut.writeUTF("Customer: "+username+" rated you, rating: "+rating+" driving skill: "+drivingSkill+" friendliness: "+friendliness+" goodmusic: "+goodmusic);
        }
    }
    private static boolean isRideOngoing(String username) {
        for (Ride r : UberServer.rides) {
            if (r.getCustomerUsername().equals(username)) {
                String status = r.getStatus();
                if (status.equalsIgnoreCase("assigned") || status.equalsIgnoreCase("in progress")) {
                    return true;
                }
            }
        }
        return false;
    }



}