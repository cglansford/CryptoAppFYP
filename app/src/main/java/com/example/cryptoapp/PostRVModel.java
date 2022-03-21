package com.example.cryptoapp;

public class PostRVModel {

    private String postTitle;
    private String postDesc;
    private String uidOfCreator;

    public PostRVModel(String postTitle, String postDesc, String uidOfCreator){
        this.postTitle = postTitle;
        this.postDesc = postDesc;
        this.uidOfCreator = uidOfCreator;
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
}
