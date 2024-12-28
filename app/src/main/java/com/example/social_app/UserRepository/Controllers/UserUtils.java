package com.example.social_app.UserRepository.Controllers;



import android.app.Activity;
import android.util.Log;

import com.example.social_app.UserRepository.HelperCallbacks.ProfileCallbacks.UserProfileValidationListner;
import com.example.social_app.UserRepository.Activities.User_info_activity;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.Utils.Common.ActivityNavigation;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.Utils.Common.FirebaseExceptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserUtils {
    public static String trimText(String text,int size) {
        if (text == null) return ""; // Handle null input
        return text.length() > size ? text.substring(0, size) : text; // Trim if longer than 12
    }



    public static void checkUserExistsAndFetchInfo(String userId, Activity activity, UserProfileValidationListner userFoundCallback) {
        // Reference to the "users" node in Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Check if the user exists in the database
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists: Retrieve user information
                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    if (userModel != null) {
                        // Successfully fetched user information
                       userFoundCallback.onUserChecked(userModel,true);
                        Log.d("UserInfo", "Fetched user info: " + userModel.toString());
                    } else {
                        // Data exists, but it is null or malformed
                        userFoundCallback.onUserChecked(null,false);
                        CustomToast.ShowCustomToast("Error fetching user data. Try again.","Failed in loading user!", "error", activity);
                    }
                } else {
                    // User does not exist
                    CustomToast.ShowCustomToast("User not found! first create a user profile.","User profile missing", "warn", activity);

                    // Navigate to User_info_activity to capture user information
                    userFoundCallback.onUserChecked(null,false);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
                Log.e("FirebaseError", "Database error: " + databaseError.getMessage());

                // Handle the error using FirebaseExceptions
                FirebaseExceptions.handleSignInError(databaseError.toException().hashCode(), activity);
            }
        });
    }
}
