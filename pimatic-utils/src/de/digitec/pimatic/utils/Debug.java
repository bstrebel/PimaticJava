package de.digitec.pimatic.utils;

import java.io.*;
import java.util.Properties;

/**
 * Created by BST on 06.01.2017.
 */


public class Debug {

    static private void _sout(String tag, String message) {
        System.out.println(String.format("[%s] %s", tag, message));
    }

    static public void v(String tag, String message) {
        _sout(tag, message);
    }
    static public void d(String tag, String message) {
        _sout(tag, message);
    }
    static public void i(String tag, String message) {
        _sout(tag, message);
    }
    static public void w(String tag, String message) {
        _sout(tag, message);
    }
    static public void e(String tag, String message) {
        _sout(tag, message);
    }
    static public void a(String tag, String message) {
        _sout(tag, message);
    }

    static public Secret getSecretFromEnvironment() {

        return new Secret(
                System.getenv("PIMATIC_USERNAME"),
                System.getenv("PIMATIC_PASSWORD"),
                System.getenv("PIMATIC_SERVER")
        );
    }

    static public Secret getSecretFromProperties() {
        Properties properties = null;
        String path = System.getProperty("user.dir") + "/Secret/PimaticPhone.properties";
        File propertyFile = new File(path);
        if (propertyFile.exists()) {
            properties = new Properties();
            InputStream input = null;
            try {
                input = new FileInputStream(propertyFile);
                properties.load(input);
                return new Secret(
                        properties.getProperty("username"),
                        properties.getProperty("password"),
                        properties.getProperty("server")
                );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public static Secret getSecretFromEnvironment(String server) {
        Secret secret = getSecretFromEnvironment();
        secret.setServer(server);
        return secret;
    }
    public static Secret getSecretFromProperties(String server) {
        Secret secret = getSecretFromProperties();
        secret.setServer(server);
        return secret;
    }
}
