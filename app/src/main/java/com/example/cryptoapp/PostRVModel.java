package com.example.cryptoapp;

import java.io.Serializable;
import java.util.ArrayList;

public class PostRVModel implements Serializable {

    private String postTitle;
    private String postDesc;
    private String uidOfCreator;
    private ArrayList<String> commentsList;

    public PostRVModel(String postTitle, String postDesc, String uidOfCreator){
        this.postTitle = postTitle;
        this.postDesc = postDesc;
        this.uidOfCreator = uidOfCreator;
        this.commentsList = new ArrayList<>();
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getUidOfCreator() {
        return uidOfCreator;
    }

    public void setUidOfCreator(String uidOfCreator) {
        this.uidOfCreator = uidOfCreator;
    }

    public ArrayList<String> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(ArrayList<String> commentsList) {
        this.commentsList = commentsList;
    }
}
