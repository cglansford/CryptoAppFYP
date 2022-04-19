package com.example.cryptoapp;

public class CommentModel {

    private String userID;
    private String comment;
    private String timeStamp;

    public CommentModel(String userID, String comment, String timeStamp){
        this.userID = userID;
        this.comment = comment;
        this.timeStamp = timeStamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
