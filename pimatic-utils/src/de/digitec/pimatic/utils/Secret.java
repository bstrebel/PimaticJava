package de.digitec.pimatic.utils;

/**
 * Created by BST on 09.01.2017.
 */
public class Secret {

    private String username;
    private String password;
    private String server;

    private String authBase64;

    public Secret(String username, String password, String server) {
        this.username = username;
        this.password = password;
        this.server = server;
    }
    public Secret(String username, String password) {
        this(username, password, "http://localhost:8080");
    }
    public Secret() {
        this("admin", "admin", "http://localhost:8080");
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getAuthBase64() {
        return authBase64;
    }

    public void setAuthBase64(String authBase64) {
        this.authBase64 = authBase64;
    }
}
