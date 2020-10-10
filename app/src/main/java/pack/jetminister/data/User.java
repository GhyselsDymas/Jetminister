package pack.jetminister.data;

import java.io.Serializable;
import java.util.HashMap;


public class User implements Serializable {
    public static final String KEY_USER_ID = "userID";
    public static final String KEY_USERS = "users";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_STREAMER_ID = "streamerID";
    public static final String KEY_STREAMER = "streamer";
    public static final String KEY_LOCATION = "serverLocation";
    public static final String KEY_IMAGE_URL = "imageURL";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_REPORTS_LOGGED = "reportsLogged";
    public static final String KEY_REPORTS_RECEIVED = "reportsReceived";
    public static final String KEY_DESCRIPTION = "description";

    private String username, password, email;
    private String imageFilename;
    private String imageURL;
    private String description;
    private String theme;
    private HashMap<String, Follow> followers, following;
    private HashMap<String, Report> reportsLogged, reportsReceived;
    private boolean streamer;
    private String serverLocation;
    private String creditCard = "";

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.imageFilename = "";
        this.imageURL = "";
        this.description = "";
        this.theme = "";
        this.followers = new HashMap<>();
        this.following = new HashMap<>();
        this.reportsLogged = new HashMap<>();
        this.reportsReceived = new HashMap<>();
        this.streamer = false;
        this.serverLocation = "eu_belgium";
        this.creditCard = "";
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public String getDescription() { return description; }

    public String getTheme() { return theme; }

    public HashMap<String, Follow>   getFollowers() {
        return followers;
    }

    public HashMap<String, Follow>   getFollowing() {
        return following;
    }

    public HashMap<String, Report> getReportsLogged() {
        return reportsLogged;
    }

    public HashMap<String, Report> getReportsReceived() {
        return reportsReceived;
    }

    public boolean isStreamer() {
        return streamer;
    }

    public String getServerLocation() { return serverLocation; }

    public String getCreditCard() { return creditCard; }



    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageFilename(String imageFilename) {
        this.imageFilename = imageFilename;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setDescription(String description) { this.description = description; }

    public void setTheme(String theme) { this.theme = theme; }

    public void setFollowers(HashMap<String, Follow>   followers) {
        this.followers = followers;
    }

    public void setFollowing(HashMap<String, Follow>   following) {
        this.following = following;
    }

    public void setReportsLogged(HashMap<String, Report> reportsLogged) {
        this.reportsLogged = reportsLogged;
    }

    public void setReportsReceived(HashMap<String, Report> reportsReceived) {
        this.reportsReceived = reportsReceived;
    }

    public void setStreamer(boolean streamer) {
        this.streamer = streamer;
    }

    public void setServerLocation(String serverLocation) { this.serverLocation = serverLocation; }

    public void setCreditCard(String creditCard) { this.creditCard = creditCard; }
}
