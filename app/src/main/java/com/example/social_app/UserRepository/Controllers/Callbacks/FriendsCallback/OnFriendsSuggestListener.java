package com.example.social_app.UserRepository.Controllers.Callbacks.FriendsCallback;

import com.example.social_app.UserRepository.Models.UserModel;

import java.util.List;

public interface OnFriendsSuggestListener {
    void OnFriendSuggestSucces(List<UserModel> suggestedFriends);
    void OnFriendsSuggestionFailure(String error);
}
