//Uber client info

public class ClientInfo {
    private int id;
    private String role;
    private String address;

    public ClientInfo(int id, String role, String address) {
        this.id = id;
        this.role = role;
        this.address = address;
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

    @Override
    public String toString() {
        return role + " ID: " + id + " | Address: " + address;
    }
}
