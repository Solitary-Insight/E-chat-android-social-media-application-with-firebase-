//package com.example.social_app.social_Repo.Controllers;
//
//import androidx.annotation.NonNull;
//
//import com.example.social_app.social_Repo.Adapters.Callbacks.OnLikedListner;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PostInteractionController {
//
//    // Firebase reference to the posts node
//
//    public static void onPostLiked(String postId, String userId, List<String> likes, OnLikedListner listener) {
//          final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");
//
//        if (likes != null) {
//            // Check if the user has already liked the post
//            for (String id : likes) {
//                if (id.equals(userId)) {
//                    // User already liked the post, so we remove the like (dislike action)
//                    likes.remove(id);  // Remove userId from the likes list
//                    // Update the Firebase database with the new likes list
//                    List<String> finalLikes = likes;
//                    postsRef.child(postId).child("likes").setValue(likes).addOnSuccessListener(unused -> {
//                        listener.onDislike(finalLikes.size());  // Notify listener with updated like count after dislike
//
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            listener.onFailed();
//                        }
//                    });
//                    return;
//                }
//            }
//
//            // If the user has not liked the post yet, add the userId to the likes list (like action)
//            likes.add(userId);  // Add the userId to the likes list
//            // Update the Firebase database with the new likes list
//            List<String> finalLikes1 = likes;
//            postsRef.child(postId).child("likes").setValue(likes).addOnSuccessListener(unused -> {
//                listener.onDislike(finalLikes1.size());  // Notify listener with updated like count after dislike
//
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    listener.onFailed();
//                }
//            });  // Notify listener with updated like count after like
//        } else {
//            // If likes list is null, initialize it and add the userId as the first like
//            likes = new ArrayList<>();
//            likes.add(userId);  // Add userId to likes list
//            // Update the Firebase database with the new likes list
//            List<String> finalLikes1 = likes;
//            postsRef.child(postId).child("likes").setValue(likes).addOnSuccessListener(unused -> {
//                listener.onDislike(finalLikes1.size());  // Notify listener with updated like count after dislike
//
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    listener.onFailed();
//                }
//            });   // Notify listener with updated like count
//        }
//    }
//
//    // Helper method to update likes in Firebase
//    private static void updatePostLikes(String postId, List<String> likes) {
//
//    }
//}
package com.example.social_app.social_Repo.Controllers;

import androidx.annotation.NonNull;

import com.example.social_app.UserRepository.Models.PostModel;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnCommentDeleteListner;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnCommmentListner;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnLikedListner;
import com.example.social_app.social_Repo.Adapters.Callbacks.OnPostDeleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostInteractionController {

    // Firebase reference to the posts node
    private static final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference("posts");

    public static void onPostLiked(String postId, String userId, List<String> likes, OnLikedListner listener) {
        // Create a copy of the likes list to avoid modifying it during iteration
        List<String> updatedLikes = new ArrayList<>(likes != null ? likes : new ArrayList<>());

        boolean alreadyLiked = updatedLikes.contains(userId);

        // If the user has already liked the post, remove the like (unlike the post)
        if (alreadyLiked) {
            updatedLikes.remove(userId);
            // Update the Firebase database with the new likes list
            postsRef.child(postId).child("likes").setValue(updatedLikes)
                    .addOnSuccessListener(aVoid -> listener.onDislike(updatedLikes.size(),updatedLikes))  // Notify listener after dislike
                    .addOnFailureListener(e -> {
                        listener.onFailed();
                        logError(e); // Log failure for debugging
                    });
        } else {
            // If the user has not liked the post, add the userId to the likes list (like the post)
            updatedLikes.add(userId);
            // Update the Firebase database with the new likes list
            postsRef.child(postId).child("likes").setValue(updatedLikes)
                    .addOnSuccessListener(aVoid -> listener.onLiked(updatedLikes.size(),updatedLikes))  // Notify listener after like
                    .addOnFailureListener(e -> {
                        listener.onFailed();
                        logError(e); // Log failure for debugging
                    });
        }
    }

    private static void logError(Exception e) {
        // Log the error for debugging purposes
        System.out.println("Error: " + e.getMessage());
    }

    public static void OnComment(String postId, String uid,String username, String comment, OnCommmentListner onCommmentListner) {
        // Step 1: Generate a unique ID for the comment
        String commentId = FirebaseDatabase.getInstance().getReference().push().getKey(); // Firebase Push ID
        if (commentId == null) {
            onCommmentListner.onCommentFailure("Failed to  comment ID.");
            return;
        }

        // Step 2: Create a new Comment object
        long timestamp = System.currentTimeMillis();
        PostModel.Comment newComment = new PostModel.Comment(commentId, uid,username, comment, timestamp,"");

        // Step 3: Add the comment to the Firebase Realtime Database
        DatabaseReference commentsRef = FirebaseDatabase.getInstance()
                .getReference("posts") // Assuming "posts" is the root node for posts
                .child(postId)
                .child("comments") // Add the comment under the "comments" node of the post
                .child(commentId);

        commentsRef.setValue(newComment)
                .addOnSuccessListener(aVoid -> {
                    // Comment successfully added
                    onCommmentListner.onCommented(commentId, newComment);
                })
                .addOnFailureListener(e -> {
                    // Comment addition failed
                    onCommmentListner.onCommentFailure(e.getMessage());
                });
    }

    public static void DeleteComment(String postId, String commentId,String comment_userId, String currentUserId, String postowner_id, String username, OnCommentDeleteListner onCommentDeleteListner) {

        String updated_text;
        if (currentUserId.equals(postowner_id) && !currentUserId.equals(comment_userId)) {
            // Post owner deletes someone else's comment
            updated_text = "⦸ _This message was deleted by the post owner_";
        } else {
            // User deletes their own comment (even if they are the post owner)
            updated_text = "⦸ _This message was deleted by " + username + "_";
        }


        // Prepare the update data in a map
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("text", updated_text);   // Update the text field
        updateData.put("delete", "deleted");         // Mark the comment as deleted
        updateData.put("timestamp",System.currentTimeMillis() );         // Mark the comment as deleted


        try{

            postsRef.child(postId).child("comments").child(commentId).
                    updateChildren(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            onCommentDeleteListner.OnCommentDeleted(updated_text);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            onCommentDeleteListner.OnCommentDeleteFailure(e.getLocalizedMessage());

                        }
                    });

        }
        catch (Exception e){
           onCommentDeleteListner.OnCommentDeleteFailure("Something went wrong !");
        }


    }

    public static void deletePost(String postid, OnPostDeleteListener onPostDeleteListener) {
        if(postid!=null && !postid.replace(" ","").equals("")){
        PostInteractionController.postsRef.child(postid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {

          @Override
          public void onSuccess(Void unused) {
          onPostDeleteListener.onPostDeleted();
          }
          }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onPostDeleteListener.onDeleteFailed(e.getLocalizedMessage());
            }
        });
    }else{
            onPostDeleteListener.onDeleteFailed("Something went wrong !");
        }
    }
}
