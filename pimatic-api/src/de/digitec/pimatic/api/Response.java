package de.digitec.pimatic.api;

import com.sun.istack.internal.Nullable;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by BST on 11.01.2017.
 */
public class Response {

    private URLConnection urlConnection;

    private byte[] content;
    private int responseCode;
    private String responseMessage;
    private Map<String, List<String>> headerFields;

    private String _contentString = null;
    private JSONObject _contentJson = null;

    public byte[] getContent() {
        return content;
    }
    public void setContent(byte[] content) {
        this.content = content;
    }
    public int getResponseCode() {
        return responseCode;
    }
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    public String getResponseMessage() {
        return responseMessage;
    }
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
    public Map<String, List<String>> getHeaderFields() {
        return headerFields;
    }
    public void setHeaderFields(Map<String, List<String>> headerFields) {
        this.headerFields = headerFields;
    }

    public Response(int responseCode, String responseMessage, Map<String, List<String>> headerFields, byte[] content) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.headerFields = headerFields;
        this.content = content;
    }

    @Override
    public String toString() {
        String result = null;
        try {
            result =  getString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getString(String charsetName) throws UnsupportedEncodingException {
        if (_contentString == null) {
            // TODO: check content typr from header field
            _contentString = new String(content, charsetName);
        }
        return _contentString;
    }
    public String getString() throws UnsupportedEncodingException {
        // TODO: get charset from header field
        return getString("UTF-8");
    }

    public JSONObject getJson() throws UnsupportedEncodingException {
        if ( _contentJson == null) {
            _contentJson = new JSONObject(getString());
        }
        return _contentJson;
    }

    public Boolean success() throws UnsupportedEncodingException {
        if (getJson().has("success")) {
            return _contentJson.getBoolean("success");
        }
        return false;
    }

}
