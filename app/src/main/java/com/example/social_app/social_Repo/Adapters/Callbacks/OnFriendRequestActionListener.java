package com.example.social_app.social_Repo.Adapters.Callbacks;


public interface OnFriendRequestActionListener {
    void onSuccess(String message);        // Notify on success
    void onFailure(String errorMessage);  // Notify on failure
}
