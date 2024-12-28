package com.example.social_app.UserRepository.Controllers.Callbacks.ProfileCallbacks;

public interface ProfileSaveListener {
    void onProfileSaved(String message);
    void onProfileSaveFailure(String error);
}
