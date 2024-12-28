package com.example.social_app.UserRepository.HelperCallbacks.ProfileCallbacks;


import com.example.social_app.UserRepository.Models.UserModel;

@FunctionalInterface
public interface UserProfileValidationListner {
    void onUserChecked(UserModel userModel, boolean userExists);
}
