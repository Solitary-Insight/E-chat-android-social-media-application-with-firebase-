package com.example.social_app.social_Repo.Adapters.Callbacks;

import com.example.social_app.UserRepository.Models.PostModel;

public interface OnCommentDeleteListner {
    void OnCommentDeleted(String updated_text);
    void  OnCommentDeleteFailure(String error);
}
