package com.example.social_app.UserRepository.Controllers;

import android.util.Log;

import com.example.social_app.UserRepository.Controllers.Callbacks.OnUserDownloadListener;
import com.example.social_app.UserRepository.Models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseUserController {


 static   public void loadUserFromFirebase(String userId , OnUserDownloadListener listener) {
        // Get Firebase reference for the 'users' node
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        // Fetch the user data based on the userId
        userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the data exists
                if (dataSnapshot.exists()) {
                    // Deserialize the User model from the snapshot
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if (user != null) {
                        // Use the user object here (e.g., display user info)
                        Log.d("Firebase", "User data loaded: " + user.getUsername());
                        listener.onUserDownloaded(user);
                    }else{
                        listener.onFailed();
                    }
                } else {
                    Log.w("Firebase", "User not found");
                    listener.onFailed();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to load user data", databaseError.toException());
                listener.onFailed();
            }
        });
    }

}
