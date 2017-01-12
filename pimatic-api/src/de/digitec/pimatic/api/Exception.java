package de.digitec.pimatic.api;

import java.io.IOException;

/**
 * Created by BST on 11.01.2017.
 */
public class Exception extends java.lang.Exception {

    java.lang.Exception orinalException;
    String message;

    public Exception(java.lang.Exception orinalException, String message) {
        super(message);
    }
}
