package de.digitec.pimatic.api;

/**
 * Created by BST on 04.01.2017.
 */

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import de.digitec.pimatic.utils.Coding;


public class PimaticApi {

    private String _server = null;
    private String _username = null;
    private String _password = null;

    private HttpURLConnection _urlConnection = null;
    private SSLContext _sslContext = null;

    private CookieManager _cookieManager = null;

    private String _authEncoded() {
        String userPassword = _username + ":" + _password;
        String encoded = Coding.EncodeBase64(userPassword);
        return encoded;
    }

    public PimaticApi(String server, String username, String password) {
        _server = server;
        _username = username;
        _password = password;
        _initConnection();
    }
    public PimaticApi(String server) { this(server, "admin", "admin"); }
    public PimaticApi() { this("http://localhost:8080" ); }

    private void _initConnection() {
        _cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(_cookieManager);
        try {
            URL url = new URL(_server);
            if (url.getProtocol() == "https") {
                _sslContext = SSLContext.getInstance("SSL");
                HttpsURLConnection.setDefaultSSLSocketFactory(_sslContext.getSocketFactory());
                _sslContext.init(null, null, new java.security.SecureRandom());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return _server;
    }

    public String login(String username, String password) {
        _username = username;
        _password = password;
        return login();
    }
    public String login() {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("username", _username);
        parms.put("password", _password);
        byte[] body = null;
        String response = null;
        try {
            body = PimaticJson.Stringify(parms).getBytes("UTF-8");
            response =  _request("POST", "/login", body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public String logout() {
        String response = null;
        try {
            response = get("/logout");
        } catch (Exception e) {
            e.printStackTrace();

        }
        return response;
    }

    private String _checkResponse(String response) {
        try {
            if (_urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                if (response.startsWith("{")) {
                    HashMap<String, Object> json = PimaticJson.Parse(response);
                    if (json.containsKey("success")) {
                        if ((Boolean) json.get("success") == true) {
                            return response;
                        }
                    }
                }
            } else {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String _request(String method, String path, byte[] body)  {


        URL url = null;
        try {
            url = new URL(_server + path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            _urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!authenticated() && !path.equals("/login")) {
            _urlConnection.setRequestProperty("Authorization", "Basic " + _authEncoded());
        }
        _urlConnection.setDoInput(true);

        if (method.equals("POST")) {
            _urlConnection.setDoOutput(true);
            _urlConnection.setFixedLengthStreamingMode(body.length);
            _urlConnection.setRequestProperty("Content-Type", "application/json");
            try {
                _urlConnection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            OutputStream os = null;
            try {
                os = _urlConnection.getOutputStream();
                os.write(body);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedInputStream in;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            if (_urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                in = new BufferedInputStream(_urlConnection.getInputStream());
            } else {
                in = new BufferedInputStream(_urlConnection.getErrorStream());
            }
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String response = null;
        try {
            // response = out.toByteArray();
            response = out.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return _checkResponse(response);
/*
        } finally {
            if (_urlConnection != null) {
                _urlConnection.disconnect();
            }
        }
*/
    }

    public String get(String path) throws Exception {
        return _request("GET", path, null);
    }

    public String getSessionCookie() {
        if (_cookieManager != null ) {
            for (HttpCookie cookie: _cookieManager.getCookieStore().getCookies()) {
                if (cookie.getName().equals("pimatic.sess"))
                    return cookie.getName();
            }
        }
        return null;
    }

    public Boolean authenticated() {
        return getSessionCookie() != null;
    }
}

