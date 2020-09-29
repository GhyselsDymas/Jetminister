package pack.jetminister.data;

public class Comment {
    private String userID;
    private String body;

    public Comment(String userID,
                   String body) {
        this.userID = userID;
        this.body = body;
    }

    public String getUserID() {
        return userID;
    }

    public String getBody() {
        return body;
    }


    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
