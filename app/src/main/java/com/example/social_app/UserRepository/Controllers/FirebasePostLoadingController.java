package com.example.social_app.UserRepository.Controllers;

import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.social_app.UserRepository.Controllers.Callbacks.FeedCallbacks.PostDownloadCallback;
import com.example.social_app.UserRepository.Models.PostModel;
import com.example.social_app.Utils.Common.NetworkManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FirebasePostLoadingController {


    public void loadFeedPostsFromFirebase(String userId, PostDownloadCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.OnPostDownloadFailure("Invalid user ID.");
            return;
        }
        if(!NetworkManager.isInternetAvailable()){
            callback.OnPostDownloadFailure("Network error, please try again later.");
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("friends");
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
        List<String> friendIds = new ArrayList<>();

        friendIds.add(userId);

        // Step 1: Fetch the user's friends
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot friendsSnapshot) {
                if (friendsSnapshot.exists() ) {
                    for (DataSnapshot friend : friendsSnapshot.getChildren()) {
                        friendIds.add(friend.getKey()); // Collect friend IDs
                    }

                    postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot postsSnapshot) {
                            List<PostModel> friendPosts = new ArrayList<>();

                            for (DataSnapshot postSnapshot : postsSnapshot.getChildren()) {
                                PostModel post = postSnapshot.getValue(PostModel.class);

                                if (post != null && post.getUserId() != null && friendIds.contains(post.getUserId())) {
                                    post.setPostid(postSnapshot.getKey());
                                    friendPosts.add(post); // Add friend's post to the list
                                }
                            }

                            callback.OnPostDownloaded(friendPosts);

                            Log.d("Firebase", "Loaded " + friendPosts.size() + " friend posts.");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            callback.OnPostDownloadFailure("Failed to load posts: " + databaseError.getMessage());
                            Log.e("Firebase", "Failed to load posts", databaseError.toException());
                        }
                    });
                    // Step 2: Fetch posts
                } else {
                    if(friendIds.size()==1){
                        callback.OnPostDownloadFailure("No friends found for user.");
                        Log.w("Firebase", "User has no friends.");
                    }

                    postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot postsSnapshot) {
                            List<PostModel> friendPosts = new ArrayList<>();

                            for (DataSnapshot postSnapshot : postsSnapshot.getChildren()) {
                                PostModel post = postSnapshot.getValue(PostModel.class);

                                if (post != null && post.getUserId() != null && friendIds.contains(post.getUserId())) {
                                    post.setPostid(postSnapshot.getKey());
                                    friendPosts.add(post); // Add friend's post to the list
                                }
                            }

                            callback.OnPostDownloaded(friendPosts);
                            Log.d("Firebase", "Loaded " + friendPosts.size() + " friend posts.");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            callback.OnPostDownloadFailure("Failed to load posts: " + databaseError.getMessage());
                            Log.e("Firebase", "Failed to load posts", databaseError.toException());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.OnPostDownloadFailure("Failed to load friends: " + databaseError.getMessage());
                Log.e("Firebase", "Failed to load friends", databaseError.toException());
            }
        });
    }


    public static void loadSearchPost(String search_data, PostDownloadCallback callback) {
        if (search_data == null || search_data.isEmpty()) {
            callback.OnPostDownloadFailure("Search data cannot be empty.");
            return;
        }
        if(!NetworkManager.isInternetAvailable()){
            callback.OnPostDownloadFailure("Network error, please try again later.");
            return;
        }

        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

        // Query to fetch all posts
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot postsSnapshot) {
                List<PostModel> searchResults = new ArrayList<>();

                for (DataSnapshot postSnapshot : postsSnapshot.getChildren()) {
                    PostModel post = postSnapshot.getValue(PostModel.class);

                    // Check if post matches the search criteria
                    if (post != null && (post.getCaption() != null )) {
                        String postContent = post.getCaption() != null ? post.getCaption().toLowerCase() : "";
                        Log.d("CONTENT",postContent);
                        if (postContent.contains(search_data.toLowerCase())) {

                            post.setPostid(postSnapshot.getKey());
                            searchResults.add(post);
                        }
                    }
                }

                if (!searchResults.isEmpty()) {
                    callback.OnPostDownloaded(searchResults);
                    Log.d("Firebase", "Search results found: " + searchResults.size());
                } else {
                    callback.OnPostDownloadFailure("No posts found matching the search criteria.");
                    Log.d("Firebase", "No search results found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.OnPostDownloadFailure("Failed to search posts: " + databaseError.getMessage());
                Log.e("Firebase", "Failed to search posts", databaseError.toException());
            }
        });
    }


}
