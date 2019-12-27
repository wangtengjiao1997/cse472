package imjames.msu.edu;

import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;

public class Cloud {

    private static final String MAGIC = "NechAtHa6RuzeR8x";
    private static final String USER = "aaaaa";
    private static final String PASSWORD = "aaaaa";
    private static final String CREATE_USER = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/user-save.php";
    private static final String LOGIN = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/user-get.php";
    private static final String ADD_USER = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/user-join.php";
    private static final String GAME_START = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/game-start.php";
    private static final String SAVE_GAME = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/save-game.php";
    private static final String GET_GAME = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/get-game.php";
    private static final String GET_TURN = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/get-turn.php";
    private static final String END_GAME = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/end-game.php";
    private static final String CHECK_WIN = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/check-win.php";
    private static final String GET_END = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/get-end.php";
    private static final String UPDATE_END = "https://webdev.cse.msu.edu/~lolloall/cse476/project2/update-end.php";

    private static final String UTF8 = "UTF-8";


    public boolean createUser(String username, String password) {
        username = username.trim();
        if(username.length() == 0) {
            return false;
        }

        password = password.trim();
        if(password.length() == 0) {
            return false;
        }

        // Create an XML packet with the information about the current image
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            xml.setOutput(writer);

            xml.startDocument("UTF-8", true);

            xml.startTag(null, "steam");
            xml.attribute(null, "usr", USER);
            xml.attribute(null, "pw", PASSWORD);
            xml.attribute(null, "magic", MAGIC);
            xml.startTag(null, "user");
            xml.attribute(null, "username", username);
            xml.attribute(null, "password", password);
            xml.endTag(null, "user");
            // view.saveXml(name, xml);
            xml.endTag(null, "steam");
            xml.endDocument();

        } catch (IOException e) {
            // This won't occur when writing to a string
            return false;
        }

        final String xmlStr = writer.toString();

        /*
         * Convert the XML into HTTP POST data
         */
        String postDataStr;
        try {
            postDataStr = "xml=" + URLEncoder.encode(xmlStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return false;
        }

        /*
         * Send the data to the server
         */
        byte[] postData = postDataStr.getBytes();

        InputStream stream = null;
        try {
            URL url = new URL(CREATE_USER);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            OutputStream out = conn.getOutputStream();
            out.write(postData);
            out.close();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xmlR = Xml.newPullParser();
                xmlR.setInput(stream, UTF8);

                xmlR.nextTag();      // Advance to first tag
                xmlR.require(XmlPullParser.START_TAG, null, "steam");

                String status = xmlR.getAttributeValue(null, "status");
                if(status.equals("no")) {
                    //String msg = xmlR.getAttributeValue(null, "msg");
                    //if(msg.equals("duplicate user"))
                    //{
                    //    return false;
                    //}
                    return false;
                }

                // We are done
            } catch(XmlPullParserException ex) {
                return false;
            } catch(IOException ex) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }
        return true;
    }


    public boolean loginUser (String user2, String password)
    {
        String query = LOGIN + "?user=" + USER + "&magic=" + MAGIC + "&pw=" + PASSWORD + "&usr=" + user2 + "&psw=" + password;
        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml2 = Xml.newPullParser();
                xml2.setInput(stream, UTF8);

                xml2.nextTag();      // Advance to first tag
                xml2.require(XmlPullParser.START_TAG, null, "steam");

                String status = xml2.getAttributeValue(null, "status");
                if(status.equals("no")) {
                    return false;
                }

                // We are done
            } catch(XmlPullParserException ex) {
                return false;
            } catch(IOException ex) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }


    public String joinUser(String username)
    {
        String usernum = "0";
        InputStream stream = null;
        String query = ADD_USER + "?user=" + USER + "&magic=" + MAGIC + "&pw=" + PASSWORD +
                "&usr=" + username;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return "0";
            }

            stream = conn.getInputStream();

            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml = Xml.newPullParser();
                xml.setInput(stream, UTF8);

                xml.nextTag();      // Advance to first tag
                xml.require(XmlPullParser.START_TAG, null, "steam");
                String status = xml.getAttributeValue(null, "status");
                 usernum = xml.getAttributeValue(null, "usernum");

                if(status.equals("no")) {
                    return "0";
                }
                // We are done
            }catch(XmlPullParserException ex) {
                return "0";
            } catch(IOException ex) {
                return "0";
            }

        } catch (MalformedURLException e) {
            return "0";
        } catch (IOException ex) {
            return "0";
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }
        return usernum;
    }

    public String isGameReady(String username) {
        String name = "";
        InputStream stream = null;
        String query = GAME_START + "?user=" + USER + "&magic=" + MAGIC + "&pw=" + PASSWORD +
                "&usr=" + username;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return "";
            }
            stream = conn.getInputStream();
            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml = Xml.newPullParser();
                xml.setInput(stream, UTF8);

                xml.nextTag();      // Advance to first tag
                xml.require(XmlPullParser.START_TAG, null, "steam");
                String status = xml.getAttributeValue(null, "status");
                String message = xml.getAttributeValue(null, "msg");
                if(status.equals("no")) {
                    return name;
                }
                int eventType = xml.next();
                if (eventType == XmlPullParser.TEXT) {
                    if (message.equals("ready"))
                    {
                        // set opponent to the one saved in the php
                        name = xml.getText();

                    }
                }
                // We are done
            }catch(XmlPullParserException ex) {
                return "";
            } catch(IOException ex) {
                return "";
            }

        } catch (MalformedURLException e) {
            return "";
        } catch (IOException ex) {
            return "";
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }
        return name;
    }



    public String checkTurn() {
        InputStream stream = null;
        String query = GET_TURN + "?user=" + USER + "&magic=" + MAGIC + "&pw=" + PASSWORD;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return "";
            }
            stream = conn.getInputStream();
            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml = Xml.newPullParser();
                xml.setInput(stream, UTF8);

                xml.nextTag();      // Advance to first tag
                xml.require(XmlPullParser.START_TAG, null, "steam");
                String status = xml.getAttributeValue(null, "status");
                String message = xml.getAttributeValue(null, "msg");
                if(status.equals("no")) {
                    return "";
                }
                    int eventType = xml.next();
                    if (eventType == XmlPullParser.TEXT) {
                        // set opponent to the one saved in the php
                        String name = xml.getText();

                        return name;

                }
                // We are done
            }catch(XmlPullParserException ex) {
                return "";
            } catch(IOException ex) {
                return "";
            }

        } catch (MalformedURLException e) {
            return "";
        } catch (IOException ex) {
            return "";
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }
        return "";
    }

    public boolean saveGameToCloud(Grid game, int win) {
        InputStream stream = null;
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xml.setOutput(writer);

            xml.startDocument("UTF-8", true);
            game.saveXML(xml);
            xml.endDocument();

        } catch (IOException e) {
            // This won't occur when writing to a string
            return false;
        }

        final String xmlStr = writer.toString();

        String query = SAVE_GAME + "?user=" + USER + "&magic=" + MAGIC + "&pw=" + PASSWORD  +
                "&xml=" + xmlStr + "&win=" + win;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode;
            try {
                responseCode = conn.getResponseCode();
            }
            catch (Exception e)
            {
                return false;
            }
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }
            stream = conn.getInputStream();
            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml2 = Xml.newPullParser();
                xml2.setInput(stream, UTF8);

                xml2.nextTag();      // Advance to first tag
                xml2.require(XmlPullParser.START_TAG, null, "steam");
                String status = xml2.getAttributeValue(null, "status");
                // String message = xml2.getAttributeValue(null, "msg");
                if (status.equals("no")) {
                    return false;
                } else {
                    return true;
                }
                // We are done
            } catch (XmlPullParserException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }
    }

    public boolean loadGameFromCloud(Grid game) {

        InputStream stream = null;

        String query = GET_GAME + "?user=" + USER + "&magic=" + MAGIC + "&pw=" + PASSWORD;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode;
            try {
                responseCode = conn.getResponseCode();
            }
            catch (Exception e)
            {
                return false;
            }
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }
            stream = conn.getInputStream();
            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml2 = Xml.newPullParser();
                xml2.setInput(stream, UTF8);


                xml2.nextTag();      // Advance to first tag
                xml2.require(XmlPullParser.START_TAG, null, "steam");
                String status = xml2.getAttributeValue(null, "status");
                if(status.equals("no")) {
                    return false;
                }

                int eventType = xml2.next();
                if (eventType == XmlPullParser.TEXT) {
                    // set opponent to the one saved in the php
                    String state = xml2.getText();

                    game.loadXML(state);
                }

                // We are done
            } catch (XmlPullParserException ex) {
                return false;
            } catch (IOException ex) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }
        return true;
    }

    public String checkWin() {

        InputStream stream = null;
        String win = "";

        String query = CHECK_WIN + "?user=" + USER + "&magic=" + MAGIC + "&pw=" + PASSWORD;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode;
            try {
                responseCode = conn.getResponseCode();
            }
            catch (Exception e)
            {
                return "";
            }
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "";
            }
            stream = conn.getInputStream();
            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml2 = Xml.newPullParser();
                xml2.setInput(stream, UTF8);


                xml2.nextTag();      // Advance to first tag
                xml2.require(XmlPullParser.START_TAG, null, "steam");
                String status = xml2.getAttributeValue(null, "status");
                if(status.equals("no")) {
                    return "";
                }

                int eventType = xml2.next();
                if (eventType == XmlPullParser.TEXT) {
                    // set opponent to the one saved in the php
                    win = xml2.getText();
                    if(win.equals("1"))
                    {
                        return "WIN";
                    }
                    else{
                        return "";
                    }
                }

                // We are done
            } catch (XmlPullParserException ex) {
                return "";
            } catch (IOException ex) {
                return "";
            }

        } catch (MalformedURLException e) {
            return "";
        } catch (IOException ex) {
            return "";
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }
        return "";
    }

    public boolean endGame ()
    {
        String query = END_GAME + "?user=" + USER + "&magic=" + MAGIC + "&pw=" + PASSWORD;
        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml2 = Xml.newPullParser();
                xml2.setInput(stream, UTF8);

                xml2.nextTag();      // Advance to first tag
                xml2.require(XmlPullParser.START_TAG, null, "steam");

                String status = xml2.getAttributeValue(null, "status");
                if(status.equals("no")) {
                    return false;
                }

                // We are done
            } catch(XmlPullParserException ex) {
                return false;
            } catch(IOException ex) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    public boolean getEnd()
    {
        String query = GET_END + "?user=" + USER + "&magic=" + MAGIC + "&pw=" + PASSWORD;
        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml2 = Xml.newPullParser();
                xml2.setInput(stream, UTF8);

                xml2.nextTag();      // Advance to first tag
                xml2.require(XmlPullParser.START_TAG, null, "steam");

                String status = xml2.getAttributeValue(null, "status");
                if(status.equals("no")) {
                    return false;
                }

                int eventType = xml2.next();
                if (eventType == XmlPullParser.TEXT) {
                    // set opponent to the one saved in the php
                    String end = xml2.getText();
                    int end2 = Integer.parseInt(end);
                    if(end2>=2)
                    {
                        return true;
                    }
                    else{
                        return false;
                    }
                }

                // We are done
            } catch(XmlPullParserException ex) {
                return false;
            } catch(IOException ex) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }
        return false;
    }


    public boolean updateEnd()
    {
        String query = UPDATE_END + "?user=" + USER + "&magic=" + MAGIC + "&pw=" + PASSWORD;
        InputStream stream = null;
        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

            /**
             * Create an XML parser for the result
             */
            try {
                XmlPullParser xml2 = Xml.newPullParser();
                xml2.setInput(stream, UTF8);

                xml2.nextTag();      // Advance to first tag
                xml2.require(XmlPullParser.START_TAG, null, "steam");

                String status = xml2.getAttributeValue(null, "status");
                if(status.equals("no")) {
                    return false;
                }
                // We are done
            } catch(XmlPullParserException ex) {
                return false;
            } catch(IOException ex) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch(IOException ex) {
                    // Fail silently
                }
            }
        }
        return true;
    }

}
