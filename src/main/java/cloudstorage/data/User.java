package cloudstorage.data;

public class User {

    private int id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private Storage storage;

    public User(int id, String username, String email, String password, Role role, Storage storage) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.storage = storage;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public Storage getStorage() {
        return storage;
    }
}
