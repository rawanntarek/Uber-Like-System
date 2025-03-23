//offer

public class Offer {
    String name;
    int rideId;
    int fare;
    Offer(String name, int rideId, int fare) {
        this.name = name;
        this.rideId = rideId;
        this.fare = fare;
    }
    public String getName() {
        return name;
    }
    public int getRideId() {

        return rideId;
    }
    public int getFare() {
        return fare;
    }
}
