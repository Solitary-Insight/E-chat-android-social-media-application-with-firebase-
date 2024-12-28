package com.example.social_app.social_Repo.Adapters.Callbacks;

import com.example.social_app.UserRepository.Models.UserModel;

import java.util.ArrayList;
import java.util.Map;

public interface OnFriendRequestsLoadedListener {
    void onFriendRequestsLoaded(ArrayList<UserModel> receivedRequests);
    void onFailure(String errorMessage);
}
