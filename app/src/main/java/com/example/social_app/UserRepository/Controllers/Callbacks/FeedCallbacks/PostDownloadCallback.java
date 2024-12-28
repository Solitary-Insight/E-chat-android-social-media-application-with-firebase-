package com.example.social_app.UserRepository.Controllers.Callbacks.FeedCallbacks;

import com.example.social_app.UserRepository.Models.PostModel;

import java.util.ArrayList;
import java.util.List;

public interface PostDownloadCallback {
    void OnPostDownloaded(List<PostModel> posts);
    void OnPostDownloadFailure(String error);
}
