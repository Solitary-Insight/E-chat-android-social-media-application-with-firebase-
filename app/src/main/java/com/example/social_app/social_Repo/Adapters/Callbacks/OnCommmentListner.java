package com.example.social_app.social_Repo.Adapters.Callbacks;

import com.example.social_app.UserRepository.Models.PostModel;

public interface OnCommmentListner {
    void onCommented(String commentId, PostModel.Comment updated_comments);
    void onCommentFailure(String error);
}
