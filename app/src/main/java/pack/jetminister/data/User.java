package pack.jetminister.data;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;

public class User {

    private String username, password, email;
//    private String imageURL;
    private String bio = "";
    private String theme = "";
    private ArrayList<String> followers, following;
    private boolean streamer = false;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User() {
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

//    public String getImageURL() {
//        return imageURL;
//    }

    public String getBio() { return bio; }

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

//    public void setImageURL(String imageURL) {
//        this.imageURL = imageURL;
//    }

    public void setBio(String bio) { this.bio = bio; }

    public void setTheme(String theme) { this.theme = theme; }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public void setFollowing(ArrayList<String>  following) {
        this.following = following;
    }

    public void setStreamer(boolean streamer) {
        this.streamer = streamer;
    }
}
