package nf.free.coursegenius.dto;

public class User {
    private int id;
    private String oid;
    private String username;
    private String email;

    public User(int id, String oid, String username, String email) {
        this.id = id;
        this.oid = oid;
        this.username = username;
        this.email = email;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}