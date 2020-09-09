package pack.jetminister.data;
import android.widget.TextView;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;


public class User implements Serializable {


    private String username, password, email;
    private String imageFilename = "";
    private String imageURL = "";
    private String description =  "Description of channel";
    private String theme = "Blank";
    private ArrayList<String> followers, following;
    private boolean streamer = false;

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

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
