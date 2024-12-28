package com.example.social_app.UserRepository.Controllers.Callbacks;

import com.example.social_app.UserRepository.Models.UserModel;

public interface OnUserDownloadListener {
    void onUserDownloaded(UserModel user);
    void onFailed();

}
