package com.example.petlocket2.Model;

public class Post {

    private String postId;
    private String postBody;
    private String postImage;
    private String postUser;

    public Post(String postId, String postBody, String postImage, String postUser)
    {
        this.postId = postId;
        this.postBody = postBody;
        this.postImage = postImage;
        this.postUser = postUser;
    }

    public Post()
    {

    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostUser() {
        return postUser;
    }

    public void setPostUser(String postUser) {
        this.postUser = postUser;
    }
}
