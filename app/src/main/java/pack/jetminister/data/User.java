package pack.jetminister.data;
import android.widget.TextView;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;

import pack.jetminister.R;


public class User implements Serializable {
    public static final String KEY_USER_ID = "userID";
    public static final String KEY_USERS = "users";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_IMAGE_URL = "imageURL";

    private String username, password, email;
    private String imageFilename = "";
    private String imageURL = "";
    private String description =  "Description of channel";
    private String theme = "Blank";
    private ArrayList<String> followers, following;
    private boolean streamer = false;
    private String location = "eu_belgium";
    private String creditCard = "";
    private Integer reportedNumber = 0;


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

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public boolean isStreamer() {
        return streamer;
    }

    public String getLocation() { return location; }

    public String getCreditCard() { return creditCard; }

    public Integer getReportedNumber() { return reportedNumber; }

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

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }

    public void setStreamer(boolean streamer) {
        this.streamer = streamer;
    }

    public void setLocation(String location) { this.location = location; }

    public void setCreditCard(String creditCard) { this.creditCard = creditCard; }

    public void setReportedNumber(Integer reportedNumber) { this.reportedNumber = reportedNumber; }
}
