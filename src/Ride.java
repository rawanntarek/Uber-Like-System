import java.util.HashMap;
import java.util.Map;

public class Ride {
    private int rideId;
    private String customerUsername;
    private String pickup;
    private String destination;
    private String status;
    private String assignedDriver;
    private Map<String, Integer> fareOffers = new HashMap<>();
    private double rating;
    private boolean isRated;

    public Ride(int rideId, String customerUsername, String pickup, String destination) {
        this.rideId = rideId;
        this.customerUsername = customerUsername;
        this.pickup = pickup;
        this.destination = destination;
        this.status = "pending";
        this.rating = 0.0;
        this.isRated = false;
    }

    public int getRideId() {
        return rideId;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public String getPickup() {
        return pickup;
    }

    public String getDestination() {
        return destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    public void addFareOffer(String driver, int fare) {
        fareOffers.put(driver, fare);
    }

    public Map<String, Integer> getFareOffers() {
        return fareOffers;
    }

    public void setAssignedDriver(String driver) {
        this.assignedDriver = driver;
        this.status = "assigned";
    }

    public String getAssignedDriver() {
        return assignedDriver;
    }

    public void setRating(double rating) {
        this.rating = rating;
        this.isRated = true;
    }

    public double getRating() {
        return rating;
    }

    public boolean isRated() {
        return isRated;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }
}
