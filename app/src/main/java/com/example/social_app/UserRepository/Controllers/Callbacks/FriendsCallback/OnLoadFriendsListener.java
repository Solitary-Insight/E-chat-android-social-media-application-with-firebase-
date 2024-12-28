package com.example.social_app.UserRepository.Controllers.Callbacks.FriendsCallback;

import com.example.social_app.UserRepository.Models.UserModel;

import java.util.List;

public interface OnLoadFriendsListener {


    public void OnFriendLoadSuccess(List<UserModel> friends);
    public void OnFriendsLoadFailure(String error);
}
