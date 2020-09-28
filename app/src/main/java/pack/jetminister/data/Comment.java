package pack.jetminister.data;

public class Comment {
//    private long commentID;
    private String userID;
//    private String username;
    private String body;

    public Comment(//long commentID,
                   String userID,
//                   String username,
                   String body) {
//        this.commentID = commentID;
        this.userID = userID;
//        this.username = username;
        this.body = body;
    }

//    public long getCommentID() {
//        return commentID;
//    }

    public String getUserID() {
        return userID;
    }

//    public String getUsername() {
//        return username;
//    }

    public String getBody() {
        return body;
    }


//    public void setCommentID(long commentID) {
//        this.commentID = commentID;
//    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

//    public void setUsername(String username) {
//        this.username = username;
//    }

    public void setBody(String body) {
        this.body = body;
    }
}
