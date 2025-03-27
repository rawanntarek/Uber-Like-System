//Uber client info

public class ClientInfo {
    private int id;
    private String role;
    private String address;
    private String username;
    private String password;
    private double rating;
    private double drivingSkills;
    private double goodMusic;
    private double friendliness;
    private int ratingCount;

    public ClientInfo(int id, String role, String address,String username, String password) {
        this.id = id;
        this.role = role;
        this.address = address;
        this.username = username;
        this.password = password;
        this.rating = 0.0;
        this.ratingCount = 0;

    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getAddress() {
        return address;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    public double getRating() {
        if (ratingCount == 0) {
            return 0.0;
        } else {
            return (rating+friendliness+goodMusic+drivingSkills) / (4*ratingCount);
        }
    }

    public void addRating(double rating,double drivingSkills,double friendliness,double goodMusic) {
        this.rating += rating;
        this.ratingCount++;
        this.drivingSkills += drivingSkills;
        this.friendliness += friendliness;
        this.goodMusic += goodMusic;
    }
    @Override
    public String toString() {
        return role + " ID: " + id + " | Address: " + address;
    }
}
