package pack.jetminister.data;

public class Follow {
    private String followID;
    private String followUsername;

    //no-argument constructor necessary for Firebase Realtime Database operations
    public Follow() {
    }

    public Follow(String followID, String followUsername) {
        this.followID = followID;
        this.followUsername = followUsername;
    }

    public String getFollowID() {
        return followID;
    }

    public String getFollowUsername() {
        return followUsername;
    }


    public void setFollowID(String followID) {
        this.followID = followID;
    }

    public void setFollowUsername(String followUsername) {
        this.followUsername = followUsername;
    }
}
