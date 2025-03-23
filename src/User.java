//user 

public class User {
    private String username;
    private String password;
    private String role;
    private double rating;
    private int ratingCount;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.rating = 0.0;
        this.ratingCount = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public double getRating() {
        return ratingCount == 0 ? 0.0 : rating / ratingCount;
    }

    public void addRating(double value) {
        this.rating += value;
        this.ratingCount++;
    }

    @Override
    public String toString() {
        return role + " | " + username + " | Rating: " + getRating();
    }
}
