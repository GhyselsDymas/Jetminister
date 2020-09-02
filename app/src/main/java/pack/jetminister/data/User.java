package pack.jetminister.data;
import com.google.firebase.database.Exclude;

public class User {

    private String userid, username, password, email, imageURL, bio, theme;
    private String[] followers, following;
    private boolean streamer = false;

    public User(String userid, String username, String password, String email, String imageURL, String bio , String theme) {
        this.userid = userid;
        this.username = username;
        this.password = password;
        this.email = email;
        this.imageURL = imageURL;
        this.bio = bio;
        this.theme = theme;
    }

    public User() {
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

    public String getBio() { return bio; }

    public String getTheme() { return theme; }

    public String[] getFollowers() {
        return followers;
    }

    public String[] getFollowing() {
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

    public void setBio(String bio) { this.bio = bio; }

    public void setTheme(String theme) { this.theme = theme; }

    public void setFollowers(String[] followers) {
        this.followers = followers;
    }

    public void setFollowing(String[] following) {
        this.following = following;
    }

    public void setStreamer(boolean streamer) {
        this.streamer = streamer;
    }
}
