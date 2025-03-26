public class ClientInfo {
    private int id;
    private String role;
    private String address;
    private String username;
    private String password;

    public ClientInfo(int id, String role, String address,String username, String password) {
        this.id = id;
        this.role = role;
        this.address = address;
        this.username = username;
        this.password = password;
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

    @Override
    public String toString() {
        return role + " ID: " + id + " | Address: " + address;
    }
}
