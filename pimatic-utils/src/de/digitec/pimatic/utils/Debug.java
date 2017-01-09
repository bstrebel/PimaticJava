package de.digitec.pimatic.utils;

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

    /*
    static public Properties properties() {
        Properties properties = null;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            String path =  Environment.getExternalStorageDirectory().getPath().toString() +
                           "/Secret/PimaticPhone.properties";
            File propertyFile = new File(path);
            if (propertyFile.exists()) {
                properties = new Properties();
                InputStream input = null;
                try {
                    input = new FileInputStream(propertyFile);
                    properties.load(input);
                    // username = properties.getProperty("username");
                    // password = properties.getProperty("password");
                    // server = properties.getProperty("server");
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
        }
        return properties;
    }
    */
}
