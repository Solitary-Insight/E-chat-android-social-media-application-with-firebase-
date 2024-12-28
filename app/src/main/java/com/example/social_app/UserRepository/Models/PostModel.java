package com.example.social_app.UserRepository.Models;

import java.util.List;
import java.util.Map;

public class PostModel {
    private String userId;                  // User who created the post

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    private String postid;                  // User who created the post
    private String username;                  // User who created the post
    private List<String> mediaUrls;         // List of media URLs (image/video)
    private String caption;                 // Caption for the post
    private long timestamp;                 // Timestamp of when the post was created
    private List<String> likes;             // List of user IDs who liked the post
    private Map<String, Comment> comments;  // Map of comment IDs to Comment objects

    // Default constructor for Firebase
    public PostModel() {
    }

    // Constructor
    public PostModel(String userId,String username, List<String> mediaUrls, String caption, long timestamp, List<String> likes, Map<String, Comment> comments) {
        this.userId = userId;
        this.mediaUrls = mediaUrls;
        this.caption = caption;
        this.timestamp = timestamp;
        this.likes = likes;
        this.comments = comments;
        this.postid="";
        this.username=username;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(List<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "PostModel{" +
                "userId='" + userId + '\'' +
                "username='" + username + '\'' +
                ", mediaUrls=" + mediaUrls +
                ", caption='" + caption + '\'' +
                ", timestamp=" + timestamp +
                ", likes=" + likes +
                ", comments=" + comments +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Nested Comment class
    public static class Comment {
        private String commentId;  // Unique ID for the comment
        private String userId;     // User who made the comment
        private String text;       // Text of the comment
        private String username;       // Text of the comment
        private long timestamp;    // Timestamp of the comment
        private String delete;

        public String isDelete() {
            return delete;
        }

        public void setDelete(String delete) {
            this.delete = delete;
        }


        // Default constructor for Firebase
        public Comment() {
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        // Constructor
        public Comment(String commentId, String userId,String username, String text, long timestamp,String delete) {
            this.commentId = commentId;
            this.userId = userId;

            this.text = text;
            this.username=username;
            this.timestamp = timestamp;
            this.delete=delete;
        }

        // Getters and Setters
        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "Comment{" +
                    "commentId='" + commentId + '\'' +
                    "delete='" + delete + '\'' +
                    ", userId='" + userId + '\'' +
                    ", text='" + text + '\'' +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
