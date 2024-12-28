package com.example.social_app.UserRepository.Models;

import java.util.List;
import java.util.Map;

public class UserModel {
    private String userId;        // Unique identifier for the user (e.g., UID from Firebase Auth)
    private String username;      // User's display name
    private String email;         // User's email address
    private String phone;         // User's phone number (optional)
    private String profilePictureUrl; // URL of the user's profile picture in Firebase Storage
    private String bio;           // User's bio or description
    private Map<String, Boolean> friends; // Map of friend userIds and their friendship status
    private Map<String, Boolean> sentFriendRequests; // Map of sent friend requests
    private Map<String, Boolean> receivedFriendRequests; // Map of received friend requests
    private List<String> posts;  // List of post IDs created by this user

    // Default constructor for Firebase
    public UserModel() {
    }

    // Constructor
    public UserModel(String userId, String username, String email, String phone, String profilePictureUrl, String bio,
                     Map<String, Boolean> friends, Map<String, Boolean> sentFriendRequests,
                     Map<String, Boolean> receivedFriendRequests, List<String> posts) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.profilePictureUrl = profilePictureUrl;
        this.bio = bio;
        this.friends = friends;
        this.sentFriendRequests = sentFriendRequests;
        this.receivedFriendRequests = receivedFriendRequests;
        this.posts = posts;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Map<String, Boolean> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, Boolean> friends) {
        this.friends = friends;
    }

    public Map<String, Boolean> getSentFriendRequests() {
        return sentFriendRequests;
    }

    public void setSentFriendRequests(Map<String, Boolean> sentFriendRequests) {
        this.sentFriendRequests = sentFriendRequests;
    }

    public Map<String, Boolean> getReceivedFriendRequests() {
        return receivedFriendRequests;
    }

    public void setReceivedFriendRequests(Map<String, Boolean> receivedFriendRequests) {
        this.receivedFriendRequests = receivedFriendRequests;
    }

    public List<String> getPosts() {
        return posts;
    }

    public void setPosts(List<String> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", bio='" + bio + '\'' +
                ", friends=" + friends +
                ", sentFriendRequests=" + sentFriendRequests +
                ", receivedFriendRequests=" + receivedFriendRequests +
                ", posts=" + posts +
                '}';
    }
}
