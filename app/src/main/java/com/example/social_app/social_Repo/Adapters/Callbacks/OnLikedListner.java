package com.example.social_app.social_Repo.Adapters.Callbacks;

import java.util.List;

public interface OnLikedListner {

    void onLiked(int likeCount, List<String> updatedLikes);
    void onDislike(int count, List<String> updatedLikes);
    void  onFailed();
}
