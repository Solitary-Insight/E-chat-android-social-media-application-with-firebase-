package com.example.social_app.UserRepository.Controllers;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.social_app.UserRepository.Controllers.Callbacks.OnMediaUploadListener;
import com.example.social_app.UserRepository.Controllers.Callbacks.ProfileCallbacks.ProfileSaveListener;
import com.example.social_app.UserRepository.Activities.User_info_activity;
import com.example.social_app.UserRepository.Models.UserModel;
import com.example.social_app.Utils.Common.CustomToast;
import com.example.social_app.Utils.Common.NetworkManager;
import com.example.social_app.social_Repo.MediaUri;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class UserFirebaseHandler {

    Activity userInfoActivity;
    public UserFirebaseHandler(Activity userInfoActivity) {
        this.userInfoActivity = userInfoActivity;
    }

    public static Boolean isUserFormValid(UserModel user, String username, String email, String phone, String bio, Uri userProfilePic, User_info_activity userInfoActivity) {

        if (username.replace(" ","").isEmpty() || email.replace(" ","").isEmpty()) {
            CustomToast.ShowCustomToast("Username and Email are required!","Feilds required", "warning", userInfoActivity);
            return false;
        } else if ( userProfilePic==null) {
            if(user!=null){
                return true;
            }
            CustomToast.ShowCustomToast("Profile picture is missing!", "Feilds required","warning", userInfoActivity);
            return false;

        }
        return  true;
    }

    public void saveUserProfile(Context context, String username, String email, String phone, String bio, Uri imageUri, UserModel user, ProfileSaveListener listener) {


        if(!NetworkManager.isInternetAvailable()){
            listener.onProfileSaveFailure("Network error, please try again later.");
            return;
        }

        FirebasePostController controller=new FirebasePostController(context);
        if(imageUri==null){
            UserModel userModel = new UserModel(
                    FirebaseAuth.getInstance().getUid(),
                    username,
                    email,
                    phone,
                    user.getProfilePictureUrl(), // Profile picture URL to be uploaded later
                    bio,
                    new HashMap<String,Boolean>(),
                    new HashMap<String,Boolean>(),
                    new HashMap<String,Boolean>(),
                    new ArrayList<>()
            );

            if(user!=null){
                userModel.setFriends(user.getFriends());
                userModel.setReceivedFriendRequests(user.getReceivedFriendRequests());
                userModel.setSentFriendRequests(user.getSentFriendRequests());
            }
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

            databaseReference.child(userModel.getUserId()).setValue(userModel)
                    .addOnSuccessListener(unused -> listener.onProfileSaved("Profile saved successfully"))
                    .addOnFailureListener(e -> listener.onProfileSaved("Profile saving failed!"));


        }
        else{
            ArrayList<MediaUri> profile_uri=new ArrayList<>();
            profile_uri.add(new MediaUri(imageUri));
            controller.UploadMedia(profile_uri, new OnMediaUploadListener() {
                @Override
                public void onUploadFailure(String error) {
                    listener.onProfileSaved("Profile saving failed!");
                    Log.d("Profile upload",error);
                }

                @Override
                public void onUploadSuccessful(ArrayList<String> urls) {
                    UserModel userModel = new UserModel(
                            FirebaseAuth.getInstance().getUid(),
                            username,
                            email,
                            phone,
                            null, // Profile picture URL to be uploaded later
                            bio,
                            new HashMap<String,Boolean>(),
                            new HashMap<String,Boolean>(),
                            new HashMap<String,Boolean>(),
                            new ArrayList<>()
                    );

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

                    userModel.setProfilePictureUrl(urls.get(0));
                    databaseReference.child(userModel.getUserId()).setValue(userModel)
                            .addOnSuccessListener(unused -> listener.onProfileSaved("Profile saved successfully"))
                            .addOnFailureListener(e -> listener.onProfileSaved("Profile saving failed!"));

                }
            });
        }





    }


}

