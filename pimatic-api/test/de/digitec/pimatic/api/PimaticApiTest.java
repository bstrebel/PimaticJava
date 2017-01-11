package de.digitec.pimatic.api;

import de.digitec.pimatic.utils.Debug;
import de.digitec.pimatic.utils.Secret;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by BST on 09.01.2017.
 */

public class PimaticApiTest {

    Session client = null;
    Secret secret = null;
    String tag = "API";

    @Before
    public void getEnvironment() {
        secret = Debug.getSecretFromProperties();
    }

    @Test
    public void create() throws Exception {

        Session client = new Session(
                secret.getServer(),
                secret.getUsername(),
                secret.getPassword()
        );
        assertEquals(client.getUrl(),secret.getServer());
    }

    /**
     * Test login/logout with session cookie
     * @throws Exception
     */
    @Test
    public void login() throws Exception {

        Session client = new Session(secret.getServer());

        String response = client.login(secret.getUsername(), secret.getPassword());
        Debug.d(tag, "[login] " + response);
        assertEquals("Login failed!", Json.Parse(response).get("success"), true);
        assertEquals("Invalid client status!",client.authenticated(), true);

        response = client.logout();
        Debug.d(tag, "[logout] " + response);
        assertEquals("Invalid client status!",client.authenticated(), false);
    }

    /**
     * Test GET request with basic authentication
     * @throws Exception
     */
    @Test
    public void config() throws Exception {
        String response = null;
        Session client = new Session(secret.getServer(), secret.getUsername(), secret.getPassword());
        // String response = client.login(username, password);
        Boolean authenticated = client.authenticated();
        response = client.get("/api/config");
        Debug.d(tag, "[config] " + response);
    }

    @Test
    public void getVariable() throws Exception {
        String variable = "PimaticApiTest";
        Session client = new Session(secret.getServer(), secret.getUsername(), secret.getPassword());
        String response = client.get("/api/variables/" + variable);
        Debug.d(tag, "[variable] " + response);
    }

    @Test
    public void setVariable() throws Exception {
        Session client = new Session(secret.getServer(), secret.getUsername(), secret.getPassword());
        String variable = "PimaticApiTest";
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("type", "value");
        parms.put("valueOrExpression", "NEW_VALUE");
        String response = client.patch("/api/variables/" + variable, parms);
        Debug.d(tag, response);
    }

    @Test
    public void postRequest() throws Exception {
        Session client = new Session(secret.getServer());
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("username", secret.getUsername());
        parms.put("password", secret.getPassword());
        String response = client.post("/login", parms);
        Debug.d(tag, response);
    }


}