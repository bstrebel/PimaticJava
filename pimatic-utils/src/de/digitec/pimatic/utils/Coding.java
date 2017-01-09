package de.digitec.pimatic.utils;

/**
 * Created by BST on 05.01.2017.
 */

import java.util.Base64;

public class Coding {

    public static String EncodeBase64(String source) {
            String encoded = Base64.getEncoder().encodeToString(source.getBytes());
            return encoded;
    }
}
