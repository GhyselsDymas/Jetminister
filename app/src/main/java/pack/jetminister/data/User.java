package pack.jetminister.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class User implements Serializable {
    public static final String KEY_USER_ID = "userID";
    public static final String KEY_USERS = "users";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_STREAMER = "streamer";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_IMAGE_URL = "imageURL";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_REPORTS_LOGGED = "reportsLogged";
    public static final String KEY_REPORTS_RECEIVED = "reportsReceived";
    public static final String KEY_DESCRIPTION = "description";

    private String username, password, email;
    private String imageFilename = "";
    private String imageURL = "";
    private String description =  "Description of channel";
    private String theme = "Blank";
    private HashMap<String, Follow> followers, following;
    private HashMap<String, Report> reportsLogged, reportsReceived;
    private boolean streamer = false;
    private String location = "eu_belgium";
    private String creditCard = "";

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
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

    public String getLocation() { return location; }

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

    public void setLocation(String location) { this.location = location; }

    public void setCreditCard(String creditCard) { this.creditCard = creditCard; }
}
