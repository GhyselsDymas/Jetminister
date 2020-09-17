package pack.jetminister.data;

public class Broadcast {
    private String username, broadcastURI;
    private int amountLikes;

    public Broadcast() {
    }

    public Broadcast(String username, String broadcastURI) {
        this.username = username;
        this.broadcastURI = broadcastURI;
        this.amountLikes = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getBroadcastURI() {
        return broadcastURI;
    }

    public int getAmountLikes() {
        return amountLikes;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setBroadcastURI(String broadcastURI) {
        this.broadcastURI = broadcastURI;
    }

    public void setAmountLikes(int amountLikes) {
        this.amountLikes = amountLikes;
    }
}
