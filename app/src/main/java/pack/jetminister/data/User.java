package pack.jetminister.data;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;


public class User implements Serializable {


    private String userid, username, password, email, imageURL, description, theme;
    private ArrayList<String> followers, following;
    private boolean streamer = false;

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.description = "Description of channel";
        this.theme = "Blank";
        this.imageURL = "https://www.rd.com/wp-content/uploads/2017/09/01-shutterstock_476340928-Irina-Bg.jpg";
    }


    @Exclude
    public String getUserid() {
        return userid;
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


    public void setUserid(String userid) {
        this.userid = userid;
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
}
