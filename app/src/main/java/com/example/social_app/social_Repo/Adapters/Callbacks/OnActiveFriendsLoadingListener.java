package com.example.social_app.social_Repo.Adapters.Callbacks;

import com.example.social_app.UserRepository.Models.UserModel;

import java.util.ArrayList;

public interface OnActiveFriendsLoadingListener {
    void onFriendsLoaded(ArrayList<UserModel> friends); // List of friends
    void onFailure(String errorMessage);               // Handle errors
}

