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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import de.digitec.pimatic.utils.Coding;


public class Session {

    private String _server = null;
    private String _username = null;
    private String _password = null;

    private HttpURLConnection _urlConnection = null;
    private SSLContext _sslContext = null;

    private CookieManager _cookieManager = null;

    public Session(String server, String username, String password) throws Exception {
        _server = server;
        _username = username;
        _password = password;
        _initConnection();
    }
    public Session(String server) throws Exception { this(server, "admin", "admin"); }
    public Session() throws Exception { this("http://localhost:8080" ); }

    private void _initConnection() throws Exception {
        try {
            URL url = new URL(_server);
            if (url.getProtocol() == "https") {
                _sslContext = SSLContext.getInstance("SSL");
                HttpsURLConnection.setDefaultSSLSocketFactory(_sslContext.getSocketFactory());
                _sslContext.init(null, null, new java.security.SecureRandom());
            }
        } catch (MalformedURLException e) {
            throw new Exception(e, "InitConnection: MalformedURLException");
            // e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(e, "InitConnection: MalformedURLException");
            // e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        _cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(_cookieManager);
    }

    public String getUrl() {
        return _server;
    }

    public Response login(String username, String password) throws Exception {
        _username = username;
        _password = password;
        return login();
    }
    public Response login() throws Exception {
        HashMap<String, Object> parms = new HashMap<>();
        parms.put("username", _username);
        parms.put("password", _password);
        byte[] body = null;
        try {
            body = Json.Stringify(parms).getBytes("UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new Exception(e, e.getMessage());
        }
        return  _request("POST", "/login", body);
    }

    public Response logout() throws Exception {
        return get("/logout");
    }

    private Response _request(String method, String path, byte[] body) throws Exception {

        Response response = null;
        URL url = null;
        try {
            url = new URL(_server + path);
        } catch (MalformedURLException e) {
            throw new Exception(e,_server + path);
        }
        try {
            _urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new Exception(e, url.getHost());
        }

        if (!authenticated() && !path.equals("/login")) {
            _urlConnection.setRequestProperty("Authorization", "Basic " + _authEncoded());
        }
        _urlConnection.setDoInput(true);

        if (method.equals("PATCH")) {
            method = "POST";
            _urlConnection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        }

        if (method.equals("POST")) {
            _urlConnection.setDoOutput(true);
            _urlConnection.setFixedLengthStreamingMode(body.length);
            _urlConnection.setRequestProperty("Content-Type", "application/json");
            try {
                _urlConnection.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new Exception(e, e.getMessage());
            }
            OutputStream os = null;
            try {
                os = _urlConnection.getOutputStream();
                os.write(body);
                os.close();
            } catch (IOException e) {
                /**
                 * Network error: invalid hostname,
                 * java.net.UnknownHostException (invalid host name)
                 * java.net.ConnectException
                 * javax.net.ssl.SSLHandshakeException: https on port 80
                 */
                throw new Exception(e, e.getMessage());
            }
        }
        BufferedInputStream in;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int responseCode = _urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
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
            response = new Response(

                    _urlConnection.getResponseCode(),
                    _urlConnection.getResponseMessage(),
                    _urlConnection.getHeaderFields(),
                    out.toByteArray()
            );
            out.close();
        } catch (IOException e) {
            /**
             * java.net.SocketException: http on on 433
             */
            throw new Exception(e, e.getMessage());
        } finally {
            if (_urlConnection != null) {
                _urlConnection.disconnect();
            }
        }
        return response;
    }

    public Response get(String path) throws Exception {
        return _request("GET", path, null);
    }

    public Response post(String path, HashMap<String , Object> data) throws Exception {

        byte[] body = null;
        try {
            body = Json.Stringify(data).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Exception(e, e.getMessage());
        }
        return _request("POST", path, body);
    }

    public Response patch(String path, HashMap<String , Object> data) throws Exception {
        byte[] body = null;
        try {
            body = Json.Stringify(data).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Exception(e, e.getMessage());
        }
        return _request("PATCH", path, body);
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

    private String _authEncoded() {
        String userPassword = _username + ":" + _password;
        String encoded = Coding.EncodeBase64(userPassword);
        return encoded;
    }

    public Boolean authenticated() {
        return getSessionCookie() != null;
    }
}

